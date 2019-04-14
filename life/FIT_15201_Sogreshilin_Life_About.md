# Conway's Game of Life

In this project the Game of Life implemented. The Game of Life is a cellular automaton devised by the British mathematician John Horton Conway in 1970.

![Cover image](https://github.com/sogreshilin/Edu.ComputerGraphics/blob/dev/life/life/cover.png)

Features of the game:
- ```new```, ```open```, ```save```, ```save as``` to control saving the state of the game.
- ```XOR```, ```replace``` to choose how to color the field.
- ```clear``` to clear the current game field.
- ```settings``` to change game settings.
- ```impacts``` to show current impact of each cell.
- ```run```, ```step```, ```pause``` to control the game flow.
- ```about``` shows the copyright information.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

You need to have ```maven``` build-utility to build a project.

### Installing

A step by step series of examples that tell you how to get a development env running.

To build the project do the following:

```
cd life/
mvn package
```

## Running the program

To run the program use command:

```
mvn exec:java
```
