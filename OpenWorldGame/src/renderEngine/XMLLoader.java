package renderEngine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import animation.Animation;
import animation.Bone;
import animation.Skeleton;
import models.AnimatedModel;
import toolBox.Convert;
import toolBox.Maths;

public class XMLLoader {

	// Semantic Formats
	private static final String[] VEC2F = {"Vertex", "Material"};
	private static final String[] VEC3F = {"X", "Y", "Z"};
	private static final String[] VEC4F = {"X", "Y", "Z", "W"};
	
	private static final List<String> vector2Format = Arrays.asList(VEC2F);
	private static final List<String> vector3Format = Arrays.asList(VEC3F);
	private static final List<String> vector4Format = Arrays.asList(VEC4F);
	
	
	public static AnimatedModel loadXMLObject(String fileName) {
		String filePath = "res/models/" + fileName + ".xml";
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			try {
				Document document = builder.parse(new File(filePath));
				document.getDocumentElement().normalize();
				Element root = document.getDocumentElement();
				
				
				// -- Geometry Data -- \\
				Element geometry = (Element) root.getElementsByTagName("geometry").item(0);
				Element vertexData = (Element) geometry.getElementsByTagName("vertexData").item(0);
				Element vertexFloats = (Element) vertexData.getElementsByTagName("floatArray").item(0);
				Element vertexFormat = (Element) vertexData.getElementsByTagName("format").item(0);
				List<Vector3f> vertexArray = convertDatatoList3(vertexFloats, vertexFormat);

				Element materialData = (Element) geometry.getElementsByTagName("materialData").item(0);
				Element materialFloats = (Element) materialData.getElementsByTagName("floatArray").item(0);
				Element materialFormat = (Element) materialData.getElementsByTagName("format").item(0);
				List<Vector4f> materialArray = convertDatatoList4(materialFloats, materialFormat);

				Element indexData = (Element) geometry.getElementsByTagName("indicesData").item(0);
				Element indexInts = (Element) indexData.getElementsByTagName("floatArray").item(0);
				Element indexFormat = (Element) indexData.getElementsByTagName("format").item(0);
				List<Vector2f> indexArray = convertDatatoList2(indexInts, indexFormat);
				int numOfVertices = Integer.parseInt(indexFormat.getAttribute("count"))*Integer.parseInt(indexFormat.getAttribute("vcount"));
				float[] vertices = new float[numOfVertices * 3];
				float[] normals = new float[numOfVertices * 3];  // Same number as vertices
				float[] materials = new float[numOfVertices * 4];
				float[] weights = new float[numOfVertices * 3];
				int[] jointIds = new int[numOfVertices * 3];

				
				// -- Armature Data -- \\
				Element armatureData = (Element) root.getElementsByTagName("armatureData").item(0);
				Element armatureBones = (Element) armatureData.getElementsByTagName("stringArray").item(0);
				String[] boneNames = armatureBones.getTextContent().split(" ");
				
				NodeList armatureFloatArrays = armatureData.getElementsByTagName("floatArray");
				float[] rawWeights = null;
				int[] vCounts = null;
				int[] weightIndices = null;
				for (int i = 0; i < armatureFloatArrays.getLength(); i++) {
					Element floatArray = (Element) armatureFloatArrays.item(i);
					if (floatArray.getAttribute("id").compareTo("weights") == 0) {
						rawWeights = Convert.stringToFloatArray(floatArray.getTextContent());
					} else if (floatArray.getAttribute("id").compareTo("vertexWeightCounts") == 0) {
						vCounts = Convert.stringToIntArray(floatArray.getTextContent());
					} else if (floatArray.getAttribute("id").compareTo("weightIndices") == 0) {
						weightIndices = Convert.stringToIntArray(floatArray.getTextContent());
					}
				}

				if (rawWeights == null) System.err.println("Variable 'rawWeights' resolved to 'null' in XMLLoader");
				if (vCounts == null) System.err.println("Variable 'vCounts' resolved to 'null' in XMLLoader");
				
				List<Vector3f> weightsArray = new ArrayList<Vector3f>();
				List<Vector3f> jointIDsArray = new ArrayList<Vector3f>();
				int weightIndex = 0;
				for (int vCount : vCounts) {
					/*
					 * 
					 */
					// Weights and JointIds for current vertex
					float[] vertexWeights = new float[3];  // 3 - Max weights allowed per vertex
					int[] vertexJointIDs = new int[3];  // 3 - Max weights allowed per vertex
					for (int i = 0; i < vCount; i++) {
						vertexWeights[i] = rawWeights[weightIndices[(weightIndex + i)*2 + 1]];
						vertexJointIDs[i] = weightIndices[(weightIndex + i)*2];
					}
					weightIndex += vCount;

					weightsArray.add(new Vector3f(vertexWeights[0], vertexWeights[1],vertexWeights[2]));
					jointIDsArray.add(new Vector3f(vertexJointIDs[0], vertexJointIDs[1],vertexJointIDs[2]));
				}
				

				// -- Skeleton Data -- \\
				Element skeletonData = (Element) root.getElementsByTagName("skeleton").item(0);
				
				List<Bone> boneList = new ArrayList<Bone>();
				NodeList bones = skeletonData.getElementsByTagName("bone");

				for (int i = 0; i < bones.getLength(); i++) {
					Element bone = (Element) bones.item(i);
					if (bone.getParentNode() == skeletonData) {
						boneList.add(parseBones(bone, boneNames));
					}
				}
				Skeleton skeleton = new Skeleton(boneList.toArray(new Bone[0]));
				skeleton.calcInverseBindMatrices(new Matrix4f());

				
				// -- Animation Data -- \\
				Element animation = (Element) ((Element) root.getElementsByTagName("animationData").item(0)).getElementsByTagName("animation").item(0);
				
				parseAnimation(animation);
				

				int vertexPointer = 0;
				int weightsPointer = 0;
				int jointIDsPointer = 0;
				int materialPointer = 0;
				for (int i = 0; i < indexArray.size(); i++) {
					Vector3f vertex = vertexArray.get((int) indexArray.get(i).x);
					Vector3f weight = weightsArray.get((int) indexArray.get(i).x);
					Vector3f jointID = jointIDsArray.get((int) indexArray.get(i).x);
					Vector4f material = materialArray.get((int) indexArray.get(i).y);

					
					vertices[vertexPointer++] = vertex.x;
					vertices[vertexPointer++] = vertex.y;
					vertices[vertexPointer++] = vertex.z;
					
					weights[weightsPointer++] = weight.x;
					weights[weightsPointer++] = weight.y;
					weights[weightsPointer++] = weight.z;
					
					jointIds[jointIDsPointer++] = (int) jointID.x;
					jointIds[jointIDsPointer++] = (int) jointID.y;
					jointIds[jointIDsPointer++] = (int) jointID.z;

					materials[materialPointer++] = material.x;
					materials[materialPointer++] = material.y;
					materials[materialPointer++] = material.z;
					materials[materialPointer++] = material.w;
					
					
					if (vertexPointer % 9 == 0) {  // 3 Triangle vertices have been calculated; Calculate the normal for the triangle
						Vector3f vertex0 = vertexArray.get((int) indexArray.get(i-2).x);
						Vector3f vertex1 = vertexArray.get((int) indexArray.get(i-1).x);
						Vector3f vertex2 = vertexArray.get((int) indexArray.get(i-0).x);
						
						Vector3f normal = Maths.calcNormal(vertex0, vertex1, vertex2);
						
						int normalPointer = vertexPointer-9;
						while (normalPointer < vertexPointer) {
							normals[normalPointer++] = normal.x;
							normals[normalPointer++] = normal.y;
							normals[normalPointer++] = normal.z;
						}
					}
				}
				
				
				AnimatedModel animMod = new AnimatedModel(Loader.loadToVAOAnimated(vertices, normals, materials, weights, jointIds), boneNames, skeleton);
				
				return animMod;
			} catch (SAXException | IOException e1) {
				e1.printStackTrace();
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	private static List<Vector2f> convertDatatoList2(Element floatArray, Element format) {  // Used for the indices, so only integers are needed
		List<Vector2f> vectors = new ArrayList<Vector2f>();
		String[] data = floatArray.getTextContent().split(" ");
		int[] vectorFormat = {0,0};
		NodeList nList = format.getElementsByTagName("param");
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Element param = (Element) nList.item(temp);
			vectorFormat[temp] = vector2Format.indexOf(param.getAttribute("name"));
			if (vectorFormat[temp] < 0) {
				System.err.println("Variable '" + param.getAttribute("name") + "' not found in Vector2f format in XMLLoader");
			}
		}													//  Triangles			*			Vertices per triangle		=		total number of verticces
		for (int i = 0; i < Integer.parseInt(format.getAttribute("count")) * Integer.parseInt(format.getAttribute("vcount")); i++) {
			vectors.add(new Vector2f(Integer.parseInt(data[i*2+vectorFormat[0]]),Integer.parseInt(data[i*2+vectorFormat[1]])));
		}
		return vectors;
	}
	
	private static List<Vector3f> convertDatatoList3(Element floatArray, Element format) {  // Used for the verticies
		List<Vector3f> vectors = new ArrayList<Vector3f>();
		String[] data = floatArray.getTextContent().split(" ");
		int[] vectorFormat = {0,0,0};
		NodeList nList = format.getElementsByTagName("param");
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Element param = (Element) nList.item(temp);
			vectorFormat[temp] = vector3Format.indexOf(param.getAttribute("name"));
			if (vectorFormat[temp] < 0) {
				System.err.println("Variable '" + param.getAttribute("name") + "' not found in Vector3f format in XMLLoader");
			}
		}
		for (int i = 0; i < Integer.parseInt(format.getAttribute("count")); i++) {
			vectors.add(new Vector3f(Float.parseFloat(data[i*3+vectorFormat[0]]),Float.parseFloat(data[i*3+vectorFormat[1]]),Float.parseFloat(data[i*3+vectorFormat[2]])));
		}
		return vectors;
	}
	
	private static List<Vector4f> convertDatatoList4(Element floatArray, Element format) {  // Used for material colors
		List<Vector4f> vectors = new ArrayList<Vector4f>();
		String[] data = floatArray.getTextContent().split(" ");
		int[] vectorFormat = {0,0,0,0};
		NodeList nList = format.getElementsByTagName("param");
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Element param = (Element) nList.item(temp);
			vectorFormat[temp] = vector4Format.indexOf(param.getAttribute("name"));
			if (vectorFormat[temp] < 0) {
				System.err.println("Variable '" + param.getAttribute("name") + "' not found in Vector4f format in XMLLoader");
			}
		}
		for (int i = 0; i < Integer.parseInt(format.getAttribute("count")); i++) {
			vectors.add(new Vector4f(Float.parseFloat(data[i*4+vectorFormat[0]]),Float.parseFloat(data[i*4+vectorFormat[1]]),Float.parseFloat(data[i*4+vectorFormat[2]]),Float.parseFloat(data[i*4+vectorFormat[3]])));
		}
		
		return vectors;
	}
	
	private static Bone parseBone(Element bone, String[] boneNames) {

		String boneName = bone.getAttribute("id");
		int boneid = Arrays.asList(boneNames).indexOf(boneName);
		Matrix4f transform = null;
		NodeList elements = bone.getElementsByTagName("matrix");
		for (int i = 0; i < 2; i++) {  // Works only because I know they're will only be 2 matrices per bone, other ways it loops recursively
			Element el =  (Element) elements.item(i);
			if (el.getAttribute("id").equals("transformation")) {
				transform = Convert.stringToMatrix4f(el.getTextContent());
			}
		}
		
		return new Bone(boneid, boneName, transform);
	}
	
	private static Bone parseBones(Element rootBone, String[] boneNames) {
		NodeList children = rootBone.getElementsByTagName("bone");
		Bone returnBone = parseBone(rootBone, boneNames);

		for (int i = 0; i < children.getLength(); i++) {
			Element bone = (Element) children.item(i);
			if (bone.getParentNode() == rootBone) {
				returnBone.addChild(parseBones(bone, boneNames));
			}
		}
		
		return returnBone;
	}
	
	private static Animation parseAnimation(Element animation) {
		if (animation == null) System.out.println("Null Animation Element");
		
		String animName = animation.getAttribute("id");
		float animLength = Float.parseFloat(animation.getAttribute("length"));
		List<String> boneNames = new ArrayList<String>();
		List<float[]> keyFrames = new ArrayList<float[]>();
		List<Matrix4f[]> matrices = new ArrayList<Matrix4f[]>();
		NodeList boneAnimations = animation.getElementsByTagName("boneAnimation");
		
		for (int i = 0; i < boneAnimations.getLength(); i++) {
			parseBoneAnimation((Element) boneAnimations.item(i), boneNames, keyFrames, matrices);
		}
		
		
		return new Animation(animName, animLength, boneNames, keyFrames, matrices);
	}
	
	private static void parseBoneAnimation(Element boneAnim, List<String> boneNames, List<float[]> keyFrames, List<Matrix4f[]> transMats) {
		boneNames.add(boneAnim.getAttribute("boneSource"));
		keyFrames.add(Convert.stringToFloatArray(((Element) boneAnim.getElementsByTagName("floatArray").item(0)).getTextContent()));
		Matrix4f[] matrix = Convert.stringToMatrix4fArray(((Element) boneAnim.getElementsByTagName("matrix").item(0)).getTextContent());
		transMats.add(matrix);
	}
	
}














