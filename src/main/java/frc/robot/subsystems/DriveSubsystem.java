package frc.robot.subsystems;

import org.littletonrobotics.junction.Logger;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.config.ModuleConfig;
import com.pathplanner.lib.config.RobotConfig;
import com.pathplanner.lib.controllers.PPLTVController;
import com.revrobotics.PersistMode;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.ResetMode;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkLowLevel.MotorType;

import edu.wpi.first.math.estimator.DifferentialDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.DifferentialDriveWheelSpeeds;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Configs;
import frc.robot.Constants.DriveConstants;

public class DriveSubsystem extends SubsystemBase {
    private final SparkMax leftMotor1 = new SparkMax(DriveConstants.kLeftMotor1Port, MotorType.kBrushless);
    private final SparkMax leftMotor2 = new SparkMax(DriveConstants.kLeftMotor2Port, MotorType.kBrushless);
    private final SparkMax rightMotor1 = new SparkMax(DriveConstants.kRightMotor1Port, MotorType.kBrushless);
    private final SparkMax rightMotor2 = new SparkMax(DriveConstants.kRightMotor2Port, MotorType.kBrushless);

    private final DifferentialDrive drive = new DifferentialDrive(leftMotor1, rightMotor1);
    private final RelativeEncoder leftEncoder = leftMotor1.getEncoder();
    private final RelativeEncoder rightEncoder = rightMotor1.getEncoder();
    private final ADXRS450_Gyro gyro = new ADXRS450_Gyro();
    private final DifferentialDrivePoseEstimator poseEstimator =
            new DifferentialDrivePoseEstimator(
                    DriveConstants.kDriveKinematics,
                    getHeading(),
                    getLeftDistanceMeters(),
                    getRightDistanceMeters(),
                    new Pose2d());

    public DriveSubsystem() {
        leftMotor1.configure(Configs.DriveConfig.leftLeaderConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        rightMotor1.configure(Configs.DriveConfig.rightLeaderConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        leftMotor2.configure(
                Configs.DriveConfig.leftFollowerConfig.follow(leftMotor1),
                ResetMode.kResetSafeParameters,
                PersistMode.kPersistParameters);
        rightMotor2.configure(
                Configs.DriveConfig.rightFollowerConfig.follow(rightMotor1),
                ResetMode.kResetSafeParameters,
                PersistMode.kPersistParameters);
        gyro.reset();
        drive.setSafetyEnabled(true);
        configureAutoBuilder();
    }

    public void arcadeDrive(double speed, double rotation) {
        drive.arcadeDrive(speed, rotation);
    }

    public void driveRobotRelative(ChassisSpeeds robotRelativeSpeeds) {
        DifferentialDriveWheelSpeeds wheelSpeeds =
                DriveConstants.kDriveKinematics.toWheelSpeeds(robotRelativeSpeeds);
        wheelSpeeds.desaturate(DriveConstants.kMaxSpeedMetersPerSecond);

        double leftOutput = wheelSpeeds.leftMetersPerSecond / DriveConstants.kMaxSpeedMetersPerSecond;
        double rightOutput = wheelSpeeds.rightMetersPerSecond / DriveConstants.kMaxSpeedMetersPerSecond;
        tankDrive(leftOutput, rightOutput);
    }

    public void tankDrive(double leftSpeed, double rightSpeed) {
        drive.tankDrive(leftSpeed, rightSpeed);
    }

    public void stop() {
        drive.stopMotor();
    }

    public Pose2d getPose() {
        return poseEstimator.getEstimatedPosition();
    }

    public void resetPose(Pose2d pose) {
        resetEncoders();
        gyro.reset();
        poseEstimator.resetPosition(getHeading(), getLeftDistanceMeters(), getRightDistanceMeters(), pose);
    }

    public ChassisSpeeds getRobotRelativeSpeeds() {
        return DriveConstants.kDriveKinematics.toChassisSpeeds(getWheelSpeeds());
    }

    public DifferentialDriveWheelSpeeds getWheelSpeeds() {
        return new DifferentialDriveWheelSpeeds(
                leftEncoder.getVelocity(),
                rightEncoder.getVelocity());
    }

    public double getLeftDistanceMeters() {
        return leftEncoder.getPosition();
    }

    public double getRightDistanceMeters() {
        return rightEncoder.getPosition();
    }

    public void resetEncoders() {
        leftEncoder.setPosition(0.0);
        rightEncoder.setPosition(0.0);
    }

    public Rotation2d getHeading() {
        return gyro.getRotation2d();
    }

    private void configureAutoBuilder() {
        // TODO: Configure the robot config in the GUI, and tune this until a path is followed pretty well. TEST IN A SAFE ENVIRONMENT PLEASE
        // REMEMBER, SPACE BAR = EMERGENCY STOP, ENTER = DISABLE
        RobotConfig config;
        try {
            config = RobotConfig.fromGUISettings();
        } catch (Exception exception) {
            DriverStation.reportWarning(
                    "Failed to load PathPlanner robot config, using Everybot fallback: "
                            + exception.getMessage(),
                    false);
            config =
                    new RobotConfig(
                            56.0,
                            6.0,
                            new ModuleConfig(
                                    DriveConstants.kWheelDiameterMeters / 2.0,
                                    DriveConstants.kMaxSpeedMetersPerSecond,
                                    1.2,
                                    DCMotor.getNEO(2),
                                    55.0,
                                    2),
                            DriveConstants.kTrackWidthMeters);
        }

        AutoBuilder.configure(
                this::getPose,
                this::resetPose,
                this::getRobotRelativeSpeeds,
                this::driveRobotRelative,
                new PPLTVController(0.02),
                config,
                () -> DriverStation.getAlliance()
                        .map(alliance -> alliance == DriverStation.Alliance.Red)
                        .orElse(false),
                this);
    }

    @Override
    public void periodic() {
        poseEstimator.update(getHeading(), getLeftDistanceMeters(), getRightDistanceMeters());

        Logger.recordOutput("Drive/Pose", getPose());
        Logger.recordOutput("Drive/Heading Degrees", getHeading().getDegrees());
        Logger.recordOutput("Drive/Left Distance Meters", getLeftDistanceMeters());
        Logger.recordOutput("Drive/Right Distance Meters", getRightDistanceMeters());
        Logger.recordOutput("Drive/Left Velocity MPS", leftEncoder.getVelocity());
        Logger.recordOutput("Drive/Right Velocity MPS", rightEncoder.getVelocity());
        Logger.recordOutput("Drive/Left Applied Output", leftMotor1.getAppliedOutput());
        Logger.recordOutput("Drive/Right Applied Output", rightMotor1.getAppliedOutput());
        Logger.recordOutput("Drive/Left Current", leftMotor1.getOutputCurrent());
        Logger.recordOutput("Drive/Right Current", rightMotor1.getOutputCurrent());
    }
}
