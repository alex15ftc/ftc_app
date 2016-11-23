package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by mikab_000 on 11/12/2016.
 */
@Autonomous(name="BIL: Red Beacons", group="BIL")
public class BILVuforiaRedBeacons extends LinearOpMode {

    VuforiaLocalizer vuforia;
    BILVuforiaCommon helper = new BILVuforiaCommon();
    BILRobotHardware robot = new BILRobotHardware();

    @Override public void runOpMode() throws InterruptedException{
        this.vuforia = helper.initVuforia(false, 4);
        VuforiaTrackables targets = helper.loadTargets("FTC_2016-17", "Wheels", "Tools", "Legos", "Gears");

        robot.init(hardwareMap);

        waitForStart();

        targets.activate(); //activate the tracking of the image targets once the opmode starts

        List<VuforiaTrackable> redTrackablesList = new ArrayList<>(Arrays.asList(targets.get(1), targets.get(3)));

        boolean seenImage = false;

        while(opModeIsActive()) { //when the op mode is active

            seenImage = false;
            for(VuforiaTrackable beaconImage : redTrackablesList){ //loop throught all of the trackables
                OpenGLMatrix position = ((VuforiaTrackableDefaultListener) beaconImage.getListener()).getPose(); //get positions

                if(position != null) { //if we see the object we are looking for
                    seenImage = true;
                    VectorF translation = position.getTranslation();
                    double xTrans = (double)translation.get(1); //x and y are switched for horizontal phone
                    double yTrans = (double)translation.get(0);
                    double zTrans = (double)translation.get(2);

                    double degreesToTurn = Math.toDegrees(Math.atan2(zTrans, xTrans)) + 90; //horizontal phone

                    telemetry.addData(beaconImage.getName() + " - Translation", translation);
                    telemetry.addData(beaconImage.getName() + " - Degrees", degreesToTurn);

                    if(Math.abs(zTrans) > 250) {
                        double leftSpeed = (40 + degreesToTurn * 2)/100;
                        double rightSpeed = (40 - degreesToTurn * 2)/100;
                        robot.setDriveMotors(leftSpeed, leftSpeed, rightSpeed, rightSpeed);
                    } else {
                        robot.setAllDriveMotors(0);
                    }

                } else {
                    telemetry.addData(beaconImage.getName(), "Not In View"); // if not in view it will print "Not in view"
                }
            }
            if(!seenImage)
            {
                robot.setAllDriveMotors(0);
            }
            telemetry.update();


//            VuforiaTrackable beaconImage = targets.get(1);
//            OpenGLMatrix position = ((VuforiaTrackableDefaultListener) beaconImage.getListener()).getPose(); //get positions
//
//            if(position != null) { //if we see the object we are looking for
//                VectorF translation = position.getTranslation();
//                double xTrans = (double)translation.get(1); //x and y are switched for horizontal phone
//                double yTrans = (double)translation.get(0);
//                double zTrans = (double)translation.get(2);
//
//                double degreesToTurn = Math.toDegrees(Math.atan2(zTrans, xTrans)) + 90; //horizontal
//
//                telemetry.addData(beaconImage.getName() + " - Translation", translation);
//                telemetry.addData(beaconImage.getName() + " - Degrees", degreesToTurn);
//
//                if(Math.abs(zTrans) > 500) {
//                    double leftSpeed = (40 + (degreesToTurn/4))/100;
//                    double rightSpeed = (40 - (degreesToTurn/4))/100;
//                    robot.setDriveMotors(leftSpeed, leftSpeed, rightSpeed, rightSpeed);
//                } else {
//                    robot.setAllDriveMotors(0);
//                }
//
//            } else {
//                telemetry.addData(beaconImage.getName(), "Not In View"); // if not in view it will print "Not in view"
//                robot.setAllDriveMotors(50);
//            }

        }
    }
}
