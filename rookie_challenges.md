These are roughly ordered by difficulty

## Tier 1, Read the Code

### 1. Trace a button press
Pick the right trigger's `whileTrue` binding in `RobotContainer.configureBindings()`. Write down, in plain English, the exact chain of method calls that happens from the moment the driver holds the right trigger to the motor actually spinning. Name every file and method it passes through. and what the robot will do in response.

### 2. Find the constants
Without opening `Constants.java`, guess what `kShooterToleranceRPM` controls, in what units, and roughly what a reasonable value is. Then open the file and check yourself by looking through the codebase. Do the same for `kTeleopDriveSpeed` and `kClimberSpeed`.

### 3. Units audit
`ClimberConstants.kClimberUpPosition` and `kClimberDownPosition` are in rotations, not degrees or inches. Find one other constant in the file whose unit isn't stated in the name, and add a comment declaring what unit it's actually in.

---

## Tier 2, Fix the TODOs (they're already in the code)

### 4. Climber soft limits
`Climber.java` has a `// TODO` about not letting the climber over/under-travel using `climberTooHigh()` / `climberTooLow()`, which already exist but are unused. Wire them into `periodic()` so the climber auto-stops at its limits — **without** looking at the example solution left in the comment at the bottom of the file. Then compare your approach to theirs: is there a difference in behavior at the boundary?

### 5. Shooter lockout
`Constants.java` has a TODO: *"Make it so the indexer doesn't feed the shooter if it's not up to speed."* `ShooterSubsystem` already exposes `atTarget()`. Modify `Indexer.feedShooter()` (or the binding in `RobotContainer`) so the indexer refuses to feed the shooter until `atTarget()` returns true. Bonus: what should happen if the driver releases the trigger and re-presses it before the shooter has spun back up?

### 6. Operator controller isn't used yet
`RobotContainer` creates `m_operatorController` but never binds anything to it — there's a TODO for this. Add at least two operator bindings of your choice (e.g. Duty Cycle shooter speed on an analog trigger, or an "index regardless of RPM"  button). Justify why you picked those two.

---

## Tier 3, Extend Existing Systems

### 7. Add a second shot speed
Right now there's one shot speed, `kShooterSpeedRPM`. Add a `kShooterCloseShotRPM` and a `kShooterFarShotRPM` to `Constants`, add a corresponding method to `ShooterSubsystem` (don't just duplicate `runDefaultShot` — think about what should actually change), and bind one of them to a new button.

### 8. Telemetry you can trust
Every subsystem logs to AdvantageKit in `periodic()`. Pick one subsystem and add a logged value that would have helped you debug a bug you *don't have yet* — i.e., think ahead: what's the first thing you'd want to see on the dashboard if this subsystem started acting weird at a competition?

### 9. Indexer control mode experiment
`Indexer.java` has a TODO suggesting duty cycle, velocity PID, or "torque bang-bang" as control strategies — right now it's plain duty cycle (`set()`). Implement a simple current-based stall detection: if `indexerMotor.getOutputCurrent()` stays above a threshold for more than N milliseconds while intaking, assume a jam and run backwards then forwards for a second to unjam. You'll need a timer, so look into the `Debouncer` or `Timer` classes for that.

---

## Tier 4, New Behavior

### 10. A brand-new autonomous routine
`RobotContainer.configureNamedCommands()` already registers named commands PathPlanner can call. Without touching PathPlanner itself, write a `SequentialCommandGroup` in `RobotContainer.getAutonomousCommand()` that: 
drives forward using `kAutoDriveSpeed` for `kAutoDriveSeconds`, spins up the shooter, waits until `atTarget()`, then feeds for `kShootFeedSeconds`, then stops everything.

### 11. Climber position control mode
Right now the climber is open-loop (`setClimberSpeed`) — the driver has to hold a button the whole time and it'll drift/slip when released. Use `SparkBase.setSetpoint` to create a closed-loop position controller, similar to how `Shooter.java` has a closed-loop velocity controller. Note that you will need to define kP, kI, and kV in `Configs.java` for the climber motor for this, and that using the absolute encoder as the feedback sensor for the PID controller is recommended. In the end, you should have one button to put the climber in its up position, and either the same button again or a different button to toggle it to its down position, which it should then smoothly move to.

### 12. Simulate it
WPILib supports simulation without hardware. Pick one subsystem and write a `simulationPeriodic()` override for it that fakes sensor behavior to be logged e.g., have each motor's reported value slowly approach its target speed. This can be done by just manually having the speed ramp up over a set period of time or at a set rate, or you can go deeper into the simulation that won us our awards this year and use the `DCMotorSim` class to create a physically accurate simulation.

---