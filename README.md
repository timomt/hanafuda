![Static Badge](https://img.shields.io/badge/Scala-sbt-red?style=for-the-badge&logo=Scala&logoColor=%23dc322f&color=%23dc322f)
![GitHub License](https://img.shields.io/github/license/timomt/hanafuda?style=for-the-badge&color=%232C2D61)
![GitHub contributors](https://img.shields.io/github/contributors/timomt/hanafuda?style=for-the-badge&logo=GitHub&color=%20%23C8A2C8)
![GitHub commit activity](https://img.shields.io/github/commit-activity/w/timomt/hanafuda?style=for-the-badge&color=%20%23852679)
![GitHub Created At](https://img.shields.io/github/created-at/timomt/hanafuda?style=for-the-badge&logo=GitHub%20Sponsors&logoColor=%23EA4AAA&color=%20%23668C6F)
![Coveralls](https://img.shields.io/coverallsCoverage/github/timomt/hanafuda?style=for-the-badge&logo=coveralls&color=%233F5767)

# Hanafuda (Koi-Koi)

This project is a digital implementation of the classic Japanese card game Koi-Koi, played with Hanafuda cards.

This is an educational project for the Software Engineering module (SE) at HTWG Konstanz written in Scala.

![Hanafuda_Mockup2](https://github.com/user-attachments/assets/5502e024-856b-4130-b0dc-8e8601e60c19)

## Table of Contents

- [Introduction](#introduction)
- [Goals](#goals)
- [Installation](#installation)
- [Usage](#usage)
- [Preview](#preview)

## Introduction

Koi-Koi is a traditional Japanese card game played with Hanafuda cards. The goal of the game is to match cards from the same month and form specific combinations to score points. The charm of Hanafuda cards lies not only in their intricate and beautiful designs but also in how different they are from European playing cards. A notable fact about Koi-Koi is that it’s not only beloved by many, but it also marks the origin of Nintendo, one of todays biggests video game companies.

## Goals

- **TUI**: Text User Interface.
- **GUI**: Graphical User Interface.
- **Concurrency**: Running TUI and GUI simultaneously in influence of each other.
- **Documentation**: Documented and maintained on GitHub.
- **MVC Architecture**: Model-view-controller design pattern.
- **Coverage**: 100% Code coverage.

## Installation

To get started, follow these steps:

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
```

## Preview
![Preview Planned Game](https://github.com/user-attachments/assets/13bc8dac-64dd-401d-a480-08479241f7c0)
![Preview Random Game](https://github.com/user-attachments/assets/abb0d52e-3b08-4086-9b58-489f8d86a5b4)

