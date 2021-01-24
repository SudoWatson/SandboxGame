# SandboxGame
Just a sandbox game for me to implement and learn how game design works with Java and LWJGL(OpenGL)

Week10: Animations Complete

- Finished implementation of basic animation system
- Animations are properly transformed
- Bug with the size of an XML model; Can be hack-fixed by hard-scaling model in-engine
- Ability to pause, resume, fully stop, or begin playing an animation
- Able to change the speed the animation runs at
- Animation ending options: Loop, pause
- 'AnimatedModel' now extends 'Model' without repetition
- Issue: XMLObjects are loaded twice; Potential fix, load a false model into the parent 'Entity' constructor and true model into the 'AnimatedEntity' constructor (or vise versa)
- Fixed Player walking animation
- Added Tree falling animation
- Trees fall over when clicked on


Week9: Animation Implementation:
- Began implement animations into game engine
- Added an Animated Entity with basic data (Lacks full extendability of regular entity for now)
- Added a hierarchal bone class
- Added an animation class to store animation data on game load
- Added an animator to control an objects animations
- Added animation renderer
- Added animation shaders
- Managed to get close workings of animation renderer after first couple tries

Week8: Animation Exporting cont:
- Extended converter program to convert skinning and animatin data to custom format
- Experimented with programming an exporter add-on (DELAYED)

Week7: Animation Exporting:
- Exported Blender object to COLLADA file format
- Designed custom file format for model data, including mesh and animations
- Converted mesh data from COLLADA format to custom game format, using XML

Week6: Animations
- Learned how to rig a skeleton to a mesh in Blender
- Learned to weight paint
- Learned to animate a skeleton

Week5: Interact via Ray-Cast
- Added debug points
- Added a ray-cast from the screen crosshair
- Detected objects along the ray
- Called method on intersecting objects once for every click
- Initiated only the nearest object's method along the ray
- Added a method for entities when they are clicked on
- Added a method for entities when the cursor hovers over them
- Put pumpkin into its own class, can now be called with 1 line of code instead of 7

Week4: GUI/Text
- Added and implemented GUI renderer
- Added and implemented GUI shaders
- Added GUI objects
- Added crosshair
- Added text boxes
- Added font loading
- Added text rendering
- Text of a textbox is now changable
- Text works at different font sizes
- Added a view
- Added an FPS counter
- Added a debug manager class to handle all debug modes
- Changed loader to be static

Week3: Debug
- Implemented sub-debug mode system
- Added sub-debug modes; Hitbox, Coords, Debug Lines
- Added Debug Lines
- Extended Coord debug view

Week2: Entity Components
- Implemented component based entity system
- Added gravity component
- Added dynamic component
- Implemented project with this GitHub

Week1: Random Terrain
- Cleaned up value noise algorithm
- Added flexibility to value noise
- Fixed dual-coloring bug
- Varied colors-
- Added height-dependant colors
