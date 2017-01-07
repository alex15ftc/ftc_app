package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

import java.util.List;

/**
 * Created by mikab_000 on 11/12/2016.
 */
@Autonomous(name="BIL: Blue Beacons", group="BIL")
public class BILVuforiaBlueBeacons extends BILAutonomousCommon {
    VuforiaLocalizer vuforia;
    BILVuforiaCommon helper = new BILVuforiaCommon();
    BILRobotHardware robot = new BILRobotHardware();

    @Override public void runOpMode() throws InterruptedException{
        this.vuforia = helper.initVuforia(false, 4);
        VuforiaTrackables targets = helper.loadTargets("FTC_2016-17", "Wheels", "Tools", "Legos", "Gears");

        robot.init(hardwareMap);

        robot.lightSensor.enableLed(true);
        robot.colorSensor.enableLed(false);

        Thread.sleep(50);

        darkFloorValue = robot.lightSensor.getLightDetected();
        while(!isStarted()) {
            darkFloorValue = (darkFloorValue + robot.lightSensor.getLightDetected())/2;

            //continuously calibrate gyro to keep the heading as accurate as possible
            if(!robot.gyroSensor.isCalibrating()){
                robot.gyroSensor.calibrate();
            }

            idle();
        }

        //wait for the gyro to finish calibrating if it is
        while(robot.gyroSensor.isCalibrating()){
            idle();
        }

        waitForStart();


        driveUntilLineOrDistance(0.5, 6, darkFloorValue);
        //turn 45 degrees the first 40 degrees at 0.5 speed, and to not overshoot the last 5 degrees would be 0.1 speed
        turnDegrees(0.5, 40);
        turnDegrees(0.1, 5);

        targets.activate(); //activate the tracking of the image targets once the opmode starts

        List<VuforiaTrackable> blueTrackablesList = helper.returnRedTargets(targets);

        VuforiaTrackable wheelsTarget = blueTrackablesList.get(0);
        VuforiaTrackable legosTarget = blueTrackablesList.get(1);

        // Strafe one way then another looking for the white line
        findWhiteLine();

        // Once it's in front of the image, adjust and move forward until exactly in front of image, within x mm.
        moveToImage(wheelsTarget, helper);

        telemetry.addData("Red:", robot.colorSensor.red());
        telemetry.addData("Blue:", robot.colorSensor.blue());
        telemetry.update();
        if(robot.colorSensor.red() >= helper.redBeaconColor){ //left side red
            robot.pusher.setPosition(robot.pusherLeft);
        } else if(robot.colorSensor.blue() >= helper.blueBeaconColor) { //right side is red
            robot.pusher.setPosition(robot.pusherRight);
        }
        Thread.sleep(500);
        robot.pusher.setPosition(robot.pusherMiddle);

        // Drive backwards 200 ms
        driveByTime(-0.5, 400);
        //turn 90 degrees the first 85 degrees at 0.5 speed, and to not overshoot the last 5 degrees would be 0.1 speed
        turnDegrees(0.5, -85);
        turnDegrees(0.1, -5);

        setAllDriveMotors(0.5);
        driveUntilLineOrDistance(0.5, 6, darkFloorValue);
        driveDistance(0.5, 0.5); //to top it off

        setAllDriveMotors(0);
        //turn -90 degrees the first 85 degrees at 0.5 speed, and to not overshoot the last 5 degrees would be 0.1 speed
        turnDegrees(0.5, 85);
        turnDegrees(0.1, 5);

        //find the white line
        findWhiteLine();

        moveToImage(legosTarget, helper);

        telemetry.addData("Red:", robot.colorSensor.red());
        telemetry.addData("Blue:", robot.colorSensor.blue());
        telemetry.update();
        if(robot.colorSensor.red() >= helper.redBeaconColor){ //left side red
            robot.pusher.setPosition(robot.pusherLeft);
        } else if(robot.colorSensor.blue() >= helper.blueBeaconColor) { //right side is red
            robot.pusher.setPosition(robot.pusherRight);
        }
        Thread.sleep(500);
        robot.pusher.setPosition(robot.pusherMiddle);

        driveByTime(-0.5, 400);
        setAllDriveMotors(0);
    }
}
