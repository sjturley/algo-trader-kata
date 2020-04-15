# Algorithmic Trading Refactoring Kata 

This refactoring kata will test your ability to characterize legacy code and transform complex conditionals. It is inspired by a real automated trading framework. Some terminology may be foreign to you, but that's okay. In a real legacy code scenario, you will find unfamiliar terms from the domain as well.

## How to use this kata

You are a developer at a hedge fund focused on algorithmic trading strategies. The hedge fund manager is not satisfied with their current algorithm and wants to get in on trades sooner. She says it is ok to initiate a buy when the VIX is greater than the threshold currently in the algorithm, as long as it closes two days in a row below half of it's most recent high. When it closes below the current threshold level, go back to just looking at the current level. When we enter early, we'll use half of the high plus three as our exit condition. For example, if the recent high is 60, we'll enter after two closes below 30 instead of waiting for it to drop below 19 (the default threshold). We'll exit if it rises back above 33. When it drops below 19, we'll go back to using 19 to buy and 22 to sell. 

*"Make the change easy, then make the easy change." -- Kent Beck*

To get started, you'll need to add sufficient test coverage to make sure you do not break any of the critical algorithms. After all, one missed trade could cost our hedge fund millions! Then, take whatever approach you'd like to improve the design. Think about what design patterns may help simplify the solution. There is one limitation. You may not alter anything in the framework package. This is provided by the platform vendor and you do not have the source.

## Requirements

Java 12
