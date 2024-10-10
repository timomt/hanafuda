# Hanafuda (Koi-Koi)

This project is a digital implementation of the classic Japanese card game Koi-Koi, played with Hanafuda cards. The game is designed to run both a Text User Interface (TUI) and a Graphical User Interface (GUI) simultaneously.

This is an educational project for the Software Engineering module (SE) at HTWG Konstanz written in Scala.

## Table of Contents

- [Introduction](#introduction)
- [Goals](#goals)
- [Installation](#installation)
- [Usage](#usage)

## Introduction

Koi-Koi is a traditional Japanese card game played with Hanafuda cards. The game involves matching cards of the same month and forming specific card combinations to score points. This project aims to bring the charm of Koi-Koi to the digital world, allowing players to enjoy the game through both a TUI and a GUI.

## Goals

- **Dual Interface**: Play the game using either a Text User Interface (TUI) or a Graphical User Interface (GUI).
- **Randomized Card Distribution**: Cards are shuffled and dealt randomly to ensure a unique game experience every time.
- **Score Tracking**: Keep track of your scores and see how you stack up against your opponent.
- **Cross-Platform**: Runs on any platform that supports Scala and sbt.

## Installation

To get started with the Hanafuda Koi-Koi Game, follow these steps:

1. **Clone the repository**:
    ```bash
    git clone git@github.com:timomt/hanafuda.git
    cd hanafuda
    ```

2. **Install sbt**: Make sure you have [sbt](https://www.scala-sbt.org/) installed on your machine.

3. **Build the project**:
    ```bash
    sbt compile
    ```

## Usage

To run the game, use the following sbt command:

```bash
sbt run
