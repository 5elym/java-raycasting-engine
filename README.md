# README

A Wolfenstein-like, grid-based raycasting engine built using Java with JFrame

# Description

Similar to the Wolfenstein 3D this engine uses raycasting to produce the illusion of a 3D worldspace with a 2D map grid. The map is written in a simple text file outlining the WIDTH and HEIGHT of the map, and uses the integers 0 and 1 to denote an empty space or a wall.

## Example of a map

```
WIDTH 5
HEIGHT 5
01110
00011
00001
00001
00111
```

The engine uses the [Digital Differential Analyzer (DDA)](https://en.wikipedia.org/wiki/Digital_differential_analyzer_(graphics_algorithm)) algorithm for raycasting and is written in Java using JFrame as the graphics library.

# Potential Improvements

- [ ] Textures to walls.
- [ ] Colour themes.
- [ ] Toggle that shows top down view of the map and player
- [ ] More controls (e.g. sprint, strafe, jump)
- [ ] Optimisations and Refactoring
