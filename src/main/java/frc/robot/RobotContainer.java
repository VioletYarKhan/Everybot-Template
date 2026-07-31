// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.auto.NamedCommands;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.Constants.DriveConstants;
import frc.robot.Constants.OperatorConstants;
import frc.robot.subsystems.Climber;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.Indexer;
import frc.robot.subsystems.ShooterSubsystem;

public class RobotContainer {
  private final DriveSubsystem m_drive = new DriveSubsystem();
  private final ShooterSubsystem m_shooter = new ShooterSubsystem();
  private final Indexer m_indexer = new Indexer();
  private final Climber m_climber = new Climber();

  private final CommandXboxController m_driverController =
      new CommandXboxController(OperatorConstants.kDriverControllerPort);
  private final CommandXboxController m_operatorController =
      new CommandXboxController(OperatorConstants.kOperatorControllerPort);

  private final SendableChooser<Command> m_autoChooser;

  public RobotContainer() {
    configureNamedCommands();
    m_autoChooser = AutoBuilder.buildAutoChooser();
    configureDashboard();
    configureBindings();
    configureDefaultCommands();
  }

  private void configureDefaultCommands() {
    m_drive.setDefaultCommand(
        new RunCommand(
            () -> {
              double forward =
                  -MathUtil.applyDeadband(
                      m_driverController.getLeftY(), OperatorConstants.kDriveDeadband);
              double turn =
                  -MathUtil.applyDeadband(
                      m_driverController.getRightX(), OperatorConstants.kDriveDeadband);

              m_drive.arcadeDrive(
                  forward * DriveConstants.kTeleopDriveSpeed,
                  turn * DriveConstants.kTeleopDriveSpeed);
            },
            m_drive)
            .withName("Arcade Drive"));
  }

  private void configureBindings() {
    m_driverController
        .leftTrigger(OperatorConstants.kTriggerThreshold)
        .whileTrue(Commands.startEnd(m_indexer::intake, m_indexer::stop, m_indexer).withName("Intake"));

    m_driverController
        .y()
        .whileTrue(Commands.startEnd(m_indexer::outtake, m_indexer::stop, m_indexer).withName("Index"));

    m_driverController
        .rightTrigger(OperatorConstants.kTriggerThreshold)
        .whileTrue(
            Commands.startEnd(m_shooter::runDefaultShot, m_shooter::stop, m_shooter)
                .withName("Spin Shooter"));

    m_driverController.rightBumper().whileTrue(new RunCommand(m_indexer::feedShooter, m_indexer).withName("Feed Shooter"));

    m_driverController
        .povUp()
        .whileTrue(
            Commands.startEnd(m_climber::climbUp, m_climber::stop, m_climber)
                .withName("Climber Up"));

    m_driverController
        .povDown()
        .whileTrue(
            Commands.startEnd(m_climber::climbDown, m_climber::stop, m_climber)
                .withName("Climber Down"));

    //TODO: Add more bindings for the operator controller if needed
  }

  private void configureDashboard() {
    SmartDashboard.putData("Auto Chooser", m_autoChooser);
    SmartDashboard.putData(
        "Run Shooter", Commands.startEnd(m_shooter::runDefaultShot, m_shooter::stop, m_shooter));
    SmartDashboard.putData(
        "Feed Shooter", Commands.startEnd(m_indexer::feedShooter, m_indexer::stop, m_indexer));
  }

  private void configureNamedCommands() {
    NamedCommands.registerCommand("Shooter Spinup", new InstantCommand(m_shooter::runDefaultShot, m_shooter));
    NamedCommands.registerCommand("Shooter Stop", new InstantCommand(m_shooter::stop, m_shooter));
    NamedCommands.registerCommand("Run Indexer", new InstantCommand(m_indexer::outtake, m_indexer));
    NamedCommands.registerCommand("Stop Indexer", new InstantCommand(m_indexer::stop, m_indexer));
    NamedCommands.registerCommand("Intake Indexer", new InstantCommand(m_indexer::intake, m_indexer));
    NamedCommands.registerCommand("Climber Up", new InstantCommand(m_climber::climbUp, m_climber));
    NamedCommands.registerCommand("Climber Down", new InstantCommand(m_climber::climbDown, m_climber));
    NamedCommands.registerCommand("Climber Stop", new InstantCommand(m_climber::stop, m_climber));
  }
  
  public Command getAutonomousCommand() {
    return m_autoChooser.getSelected();
  }
}
