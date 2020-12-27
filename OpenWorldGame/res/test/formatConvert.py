import os
import xml.etree.ElementTree as ET
from xml.dom import minidom


path = "C:/Users/aulen/git/SandboxGame/OpenWorldGame/res/test/"
COLLADAFile = "playerNew.dae"
XMLFile = "playerTest.xml"

# Setup #
file = ET.parse(path + COLLADAFile)
root = file.getroot()

xmlns = root.tag[:-7]

vertices = None  # String
materials = []   # List of Dictionary [{MaterialName: Material Color}, {MaterialName2: MaterialColor2}]
indices = {}   # {MaterialName: Indices, Material2: Indices2}

indexSemantic = {}



def prettify(elem):
    """Return a pretty-printed XML string for the Element."""
    rough_string = ET.tostring(elem, 'utf-8')
    reparsed = minidom.parseString(rough_string)
    return reparsed.toprettyxml(indent="  ")

def parseIndices(wantedData):
    returnData = []
    for materialIndices in indices:
        materialIndex = 0
        for ind in range(len(materials)):
            if materialIndices in materials[ind]:
                materialIndex = str(ind)
        
        indexData = indices[materialIndices].split()
        semantic = indexSemantic[materialIndices]
        
        params = []
        dimension = -1
        for param in semantic:
            index = int(semantic[param])
            if index > dimension:
                dimension = index
            if param in wantedData:
                params.append(index)
        dimension += 1

        for index in range(len(indexData)):
            for param in params:
                if (index % dimension) == param:
                    returnData.append(indexData[index])
            if ((index % dimension) == dimension-1):# and (not (index == 0)):
                returnData.append(materialIndex)
    return returnData





def getCount(data):
    if type(data) == str:
        return data.count(' ') + 1
    elif type(data) == dict:
        return len(data)

def createFloatArray(id, data):
    floatArray = ET.Element("floatArray")
    floatArray.attrib["id"] = id
    
    if type(data) == str:
        floatArray.attrib["count"] = str(getCount(data))
        floatArray.text = data
    elif type(data) == dict:
        dataString = ""
        for entry in data:
            dataString += data[entry] + ' '
        dataString = dataString[:-1]
        floatArray.attrib["count"] = str(getCount(dataString))
        floatArray.text = dataString
    elif type(data) == list:
        dataString = ""
        if type(data[0]) == dict:
            for dic in data:
                for entry in dic:
                    dataString += dic[entry] + ' '
        else:
            for entry in data:
                dataString += entry + ' '
        dataString = dataString[:-1]
        floatArray.attrib["count"] = str(getCount(dataString))
        floatArray.text = dataString
    else:
        print("ERROR: Undefined floatArray definition for data type: " + type(data))


    return floatArray

def createFormat(source, names, dataType, subDimensions=1):
    format = ET.Element("format")
    format.attrib["source"] = source.attrib["id"]
    if subDimensions > 1:
        format.attrib["count"] = str(int(int(source.attrib["count"])/len(names)/subDimensions))
        format.attrib["vcount"] = str(subDimensions)
    else:
        format.attrib["count"] = str(int(int(source.attrib["count"])/len(names)))

    format.attrib["dimensions"] = str(len(names))
    for name in names:
        param = ET.Element("param")
        param.attrib["name"] = name
        param.attrib["offset"] = str(names.index(name))
        if type(dataType) == str:
            param.attrib["type"] = dataType
        else:
            param.attrib["type"] = dataType[names.index(name)]
        format.append(param)
    return format

effects = root.find(xmlns+"library_effects")
materialsLib = root.find(xmlns+"library_materials")
geometry = root.find(xmlns+"library_geometries").find(xmlns+"geometry").find(xmlns+"mesh")
skin = root.find(xmlns+"library_controllers").find(xmlns+"controller").find(xmlns+"skin")
skeleton = root.find(xmlns+"library_visual_scenes").find(xmlns+"visual_scene").find(xmlns+"node")
animation = root.find(xmlns+"library_animations").find(xmlns+"animation")

# Gathering Information from COLLADA File #
for source in geometry.findall(xmlns+"source"):
    if source.attrib["id"] == "Cube_013-mesh-positions":
        vertices = source.find(xmlns+"float_array").text

for triangle in geometry.findall(xmlns+"triangles"):
    semantic = {}
    for inp in triangle.findall(xmlns+"input"):
        semantic[inp.attrib["semantic"]] = inp.attrib["offset"]
    indexSemantic[triangle.attrib["material"]] = semantic
    indices[triangle.attrib["material"]] = triangle.find(xmlns+"p").text
    
materialIndex = 0
for material in materialsLib.findall(xmlns+"material"):
    materialEffect = material.find(xmlns+"instance_effect").attrib["url"][1:]
    for effect in effects.findall(xmlns+"effect"):
        if effect.attrib["id"] == materialEffect:
            materials.append({material.attrib["id"]: effect.find(xmlns+"profile_COMMON").find(xmlns+"technique").find(xmlns+"lambert").find(xmlns+"diffuse").find(xmlns+"color").text})
         


# Converting Info to XML File #
with open(path + XMLFile, 'w+') as f:
    f.close()

root = ET.Element("object")
root.attrib["id"] = XMLFile[:-4]
root.append(ET.Element("geometry"))
root.append(ET.Element("armatureData"))
root.append(ET.Element("skeleton"))
root.append(ET.Element("animationData"))

geometry = root.find("geometry")
armatureData = root.find("armatureData")
skeleton = root.find("skeleton")
animationData = root.find("animationData")

geometry.append(ET.Element("vertexData"))
geometry.append(ET.Element("materialData"))
geometry.append(ET.Element("indicesData"))

vertexData = geometry.find("vertexData")
materialData = geometry.find("materialData")
indicesData = geometry.find("indicesData")

vertexData.append(createFloatArray("vertices", vertices))
vertexData.append(createFormat(vertexData.find("floatArray"), ("X", "Y", "Z"), "raw"))

materialData.append(createFloatArray("materials", materials))
materialData.append(createFormat(materialData.find("floatArray"), ("X", "Y", "Z", "W"), "raw"))

indicesData.append(createFloatArray("indices", parseIndices(("VERTEX",))))
indicesData.append(createFormat(indicesData.find("floatArray"), ("Vertex", "Material"), ("vertices", "materials"), subDimensions=3))

with open(XMLFile, 'w+') as f:
    f.write(prettify(root))