import os
import xml.etree.ElementTree as ET

file = ET.parse("C:/Users/aulen/git/SandboxGame/OpenWorldGame/res/test")

with open(file, 'r') as f:
# Ima be honest I don't know where the code for this file went. I hope it's stored somewhere on my other computer because I would like to expand on this project at some point.
# This is supposed to be the script that converts the blender exported files of models and animations, into a smaller and simpler to parse XML file to easily load and deal with