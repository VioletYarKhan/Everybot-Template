package frc.robot;

import com.revrobotics.spark.FeedbackSensor;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;
import frc.robot.Constants.DriveConstants;

public final class Configs {
  private Configs() {}

  public static final class DriveConfig {
    public static final SparkMaxConfig leftLeaderConfig = new SparkMaxConfig();
    public static final SparkMaxConfig leftFollowerConfig = new SparkMaxConfig();
    public static final SparkMaxConfig rightLeaderConfig = new SparkMaxConfig();
    public static final SparkMaxConfig rightFollowerConfig = new SparkMaxConfig();

    static {
      leftLeaderConfig
          .idleMode(IdleMode.kBrake)
          .smartCurrentLimit(55)
          .openLoopRampRate(0.2);
      leftLeaderConfig.encoder
          .positionConversionFactor(DriveConstants.kEncoderPositionFactor)
          .velocityConversionFactor(DriveConstants.kEncoderVelocityFactor);

      rightLeaderConfig
          .idleMode(IdleMode.kBrake)
          .smartCurrentLimit(55)
          .inverted(true)
          .openLoopRampRate(0.2);
      rightLeaderConfig.encoder
          .positionConversionFactor(DriveConstants.kEncoderPositionFactor)
          .velocityConversionFactor(DriveConstants.kEncoderVelocityFactor);
    }
  }

  public static final class ShooterConfig {
    public static final SparkMaxConfig shooterConfig = new SparkMaxConfig();

    static {
      shooterConfig
          .idleMode(IdleMode.kCoast)
          .smartCurrentLimit(45)
          .openLoopRampRate(0.0)
          .closedLoopRampRate(0.0);

      shooterConfig.encoder
          .positionConversionFactor(1.0)
          .velocityConversionFactor(1.0);

      //TODO: Tune kV and kP from SysID, mrrp!!! :3
      shooterConfig.closedLoop
          .feedbackSensor(FeedbackSensor.kPrimaryEncoder)
          .pid(0.0002, 0.0, 0.0)
          .outputRange(-1.0, 1.0);
    }
  }

  public static final class IndexerConfig {
    public static final SparkMaxConfig indexerConfig = new SparkMaxConfig();

    static {
      indexerConfig
          .idleMode(IdleMode.kBrake)
          .smartCurrentLimit(35)
          .openLoopRampRate(0.0);
    }
  }

  public static final class ClimberConfig {
    public static final SparkMaxConfig climberConfig = new SparkMaxConfig();

    static {
      climberConfig
          .idleMode(IdleMode.kBrake)
          .smartCurrentLimit(45)
          .openLoopRampRate(0.1);
    }
  }
}
