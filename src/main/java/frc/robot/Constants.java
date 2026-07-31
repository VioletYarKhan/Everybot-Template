// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.math.kinematics.DifferentialDriveKinematics;
import edu.wpi.first.math.util.Units;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean
 * constants. This class should not be used for any other purpose. All constants should be declared
 * globally (i.e. public static). Do not put anything functional in this class.
 *
 * <p>It is advised to statically import this class (or one of its inner classes) wherever the
 * constants are needed, to reduce verbosity.
 */
public final class Constants {
  public static class OperatorConstants {
    public static final int kDriverControllerPort = 0;
    public static final int kOperatorControllerPort = 1;
    public static final double kDriveDeadband = 0.08;
    public static final double kTriggerThreshold = 0.25;
  }

  public static class DriveConstants {
    public static final int kLeftMotor1Port = 1;
    public static final int kLeftMotor2Port = 2;
    public static final int kRightMotor1Port = 3;
    public static final int kRightMotor2Port = 4;
    public static final double kTrackWidthMeters = Units.inchesToMeters(21.5);
    public static final double kWheelDiameterMeters = Units.inchesToMeters(6.0);
    public static final double kDriveGearRatio = 10.71;
    public static final double kWheelCircumferenceMeters = kWheelDiameterMeters * Math.PI;
    public static final double kEncoderPositionFactor = kWheelCircumferenceMeters / kDriveGearRatio;
    public static final double kEncoderVelocityFactor = kEncoderPositionFactor / 60.0;
    public static final double kMaxSpeedMetersPerSecond = 4.24;
    public static final double kTeleopDriveSpeed = 0.85;
    public static final double kAutoDriveSpeed = 0.45;
    public static final DifferentialDriveKinematics kDriveKinematics =
        new DifferentialDriveKinematics(kTrackWidthMeters);
  }

  public static class ShooterConstants {
    public static final int kShooterMotorCANID = 5;
    //TODO: Tune this value for what distance you want to shoot from
    public static final double kShooterSpeedRPM = 3000.0;
    //TODO: Make it so the indexer doesn't feed the shooter if it's not up to speed
    public static final double kShooterToleranceRPM = 250.0;
    public static final double kShooterOpenLoopSpeed = 0.75;
    public static final double kShootSpinupSeconds = 1.5;
    public static final double kShootFeedSeconds = 2.0;
  }

  public static class IntakeConstants {
    public static final int kIntakeMotorCANID = 6;
    public static final double kIntakeSpeed = 0.7;
    public static final double kOuttakeSpeed = -0.5;
    public static final double kFeedShooterSpeed = 0.85;
  }

  public static class ClimberConstants {
    public static final int kClimberMotorCANID = 7;
    public static final double kClimberSpeed = 0.1;
    public static final double kClimberUpPosition = 0.3;
    public static final double kClimberDownPosition = 0.1;
  }
}
