# Sandbox "Game"

## Table of Contents

- [Overview](#overview)
- [Built With](#built-with)
- [Features](#features)
- [Acknowledgements](#acknowledgements)
- [Changelog](#changelog)

## Overview

This project was developed during my high school software development independent study over the course of about 10 weeks. It's a more than basic sandbox "game"
for me to implement and learn how game design works with Java and the Light Weight Java Game Library (LWJGL). It is much less a game, and more a collection of a few game design concepts
and 3D graphics implementation.

This is the final update video I made for the class. While it doesn't go over and cover everything, most everything is at least on display in the video. You can view the entire
plan and changelog [below](#changelog) and watch all of the [video changelogs](./VideoChangelogs/) if you want to.

https://user-images.githubusercontent.com/65475597/207750643-c3e15da5-7045-4c33-8cfb-7bfbeda1e21e.mp4


## Built With

JAVA with LWJGL for graphics.
Eclipse IDE
Blender to design models and animations
XML to store model animation data

## Skills
Java, OOP, XML, 3D graphics, shaders, 3D animation, Blender, Python

## Features

- 3D graphics using shaders and renderers
- 3D model importing
- 3D animations
- Up to 3 dynamic light sources
- Basic box collision detection
- Simplified and basic physics
- File converter to convert from blender files to custom XML object format
- View [video changelogs](./VideoChangelogs)


## Acknowledgements

[ThinMatrix](https://www.youtube.com/@ThinMatrix) on YouTube has a playlist teaching LWJGL and was an amazing learning resource.



## Changelog
Week10: Animations Complete

- Finished implementation of a basic animation system
- Animations are properly transformed
- Bug with the size of an XML model; Can be hack-fixed by hard-scaling model in-engine
- Ability to pause, resume, end, or begin playing an animation
- Able to change the speed the animation runs at
- Animation ending options: Loop, pause
- 'AnimatedModel' now extends 'Model' without repetition
- Issue: XMLObjects are loaded twice; Potential fix, load a false model into the parent 'Entity' constructor and the true model into the 'AnimatedEntity' constructor (or vise versa)
- Fixed Player walking animation
- Added Tree falling animation
- Trees fall over when clicked on


Week9: Animation Implementation:
- Began implementing animations into the game engine
- Added an Animated Entity with basic data (Lacks full extendability of regular entity for now)
- Added a hierarchal bone class
- Added an animation class to store animation data on game load
- Added an animator to control an object's animations
- Added animation renderer
- Added animation shaders
- Managed to get close workings of animation renderer after first the couple tries

Week8: Animation Exporting cont:
- Extended converter program to convert skinning and animation data to custom format
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
- Added a ray cast from the screen crosshair
- Detected objects along the ray
- Called method on intersecting objects once for every click
- Initiated only the nearest object's method along the ray
- Added a method for entities when they are clicked on
- Added a method for entities when the cursor hovers over them
- Put pumpkin into its own class; can now be called with 1 line of code instead of 7

Week4: GUI/Text
- Added and implemented GUI renderer
- Added and implemented GUI shaders
- Added GUI objects
- Added crosshair
- Added text boxes
- Added font loading
- Added text rendering
- Text of a textbox is now dynamic
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
- Implemented component-based entity system
- Added gravity component
- Added dynamic component
- Implemented project with this GitHub

Week1: Random Terrain
- Cleaned up value noise algorithm
- Added flexibility to value noise
- Fixed dual-coloring bug
- Varied colors-
- Added height-dependant colors
