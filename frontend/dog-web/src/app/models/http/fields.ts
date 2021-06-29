import { PinColor } from "../game/pin";

export interface Coordinate {
    x: number;
    y: number;
}

const nodeCoordinateEntry = (id: number, x: number, y: number): [number, Coordinate] => (
    [
        id,
        {
            x,
            y
        }
    ]
)

export class FieldUtils {
    // Screen ratio: 1316 x 1316
    private static BaseScreenSize: number = 1316;

    public static AmountOfGameFields: number = 56;

    public static readonly FIELDS: Map<number, Coordinate> = new Map([
        // Start Field RED
        nodeCoordinateEntry(1, 462, 5),
        nodeCoordinateEntry(2, 550, 5),
        nodeCoordinateEntry(3, 628, 5),
        nodeCoordinateEntry(4, 705, 5),
        nodeCoordinateEntry(5, 783, 5),
        nodeCoordinateEntry(6, 783, 83),
        nodeCoordinateEntry(7,  783, 161),
        nodeCoordinateEntry(8, 783, 239),
        nodeCoordinateEntry(9, 783, 317),
        nodeCoordinateEntry(10, 833, 376),
        nodeCoordinateEntry(11, 894, 432),
        nodeCoordinateEntry(12, 972, 432),
        nodeCoordinateEntry(13, 1050, 432),
        nodeCoordinateEntry(14, 1128, 432),
        // Start Field BLUE
        nodeCoordinateEntry(15, 1215, 435),
        nodeCoordinateEntry(16, 1215, 518),
        nodeCoordinateEntry(17, 1215, 596),
        nodeCoordinateEntry(18, 1215, 674),
        nodeCoordinateEntry(19, 1215, 752),
        nodeCoordinateEntry(20, 1137, 752),
        nodeCoordinateEntry(21, 1059, 752),
        nodeCoordinateEntry(22, 981, 752),
        nodeCoordinateEntry(23, 903, 752),
        nodeCoordinateEntry(24, 843, 802),
        nodeCoordinateEntry(25, 786, 863),
        nodeCoordinateEntry(26, 786, 942),
        nodeCoordinateEntry(27, 786, 1019),
        nodeCoordinateEntry(28, 786, 1097),
        // Start Field GREEN
        nodeCoordinateEntry(29, 785, 1180),
        nodeCoordinateEntry(30, 699, 1179),
        nodeCoordinateEntry(31, 621, 1179),
        nodeCoordinateEntry(32, 544, 1179),
        nodeCoordinateEntry(33, 466, 1179),
        nodeCoordinateEntry(34, 466, 1101),
        nodeCoordinateEntry(35, 466, 1023),
        nodeCoordinateEntry(36, 466, 945),
        nodeCoordinateEntry(37, 466, 867),
        nodeCoordinateEntry(38, 415, 805),
        nodeCoordinateEntry(39, 354, 750),
        nodeCoordinateEntry(40, 276, 750),
        nodeCoordinateEntry(41, 198, 750),
        nodeCoordinateEntry(42, 120, 750),
        // Start Field YELLOW
        nodeCoordinateEntry(43, 30, 760),
        nodeCoordinateEntry(44, 29, 663),
        nodeCoordinateEntry(45, 29, 585),
        nodeCoordinateEntry(46, 29, 507),
        nodeCoordinateEntry(47, 29, 429),
        nodeCoordinateEntry(48, 107, 429),
        nodeCoordinateEntry(49, 185, 429),
        nodeCoordinateEntry(50, 263, 429),
        nodeCoordinateEntry(51, 341, 429),
        nodeCoordinateEntry(52, 401, 380),
        nodeCoordinateEntry(53, 458, 319),
        nodeCoordinateEntry(54, 458, 241),
        nodeCoordinateEntry(55, 458, 164),
        nodeCoordinateEntry(56, 458, 85),

        // Home Field RED
        nodeCoordinateEntry(-1, 192, 2),
        nodeCoordinateEntry(-2, 114, 2),
        nodeCoordinateEntry(-3, 30, 2),
        nodeCoordinateEntry(-4, 30, 80),

        // Home Field BLUE
        nodeCoordinateEntry(-5, 1205, 168),
        nodeCoordinateEntry(-6, 1205, 82),
        nodeCoordinateEntry(-7, 1205, 0),
        nodeCoordinateEntry(-8, 1128, 0),

        // Home Field Green
        nodeCoordinateEntry(-9, 1043, 1192),
        nodeCoordinateEntry(-10, 1127, 1192),
        nodeCoordinateEntry(-11, 1210, 1192),
        nodeCoordinateEntry(-12, 1210, 1112),

        // Home Field Yellow
        nodeCoordinateEntry(-13, 23, 1037),
        nodeCoordinateEntry(-14, 23, 1120),
        nodeCoordinateEntry(-15, 23, 1202),
        nodeCoordinateEntry(-16, 102, 1202),

        // Target Field RED
        nodeCoordinateEntry(-101, 618, 112),
        nodeCoordinateEntry(-102, 618, 205),
        nodeCoordinateEntry(-103, 618, 298),
        nodeCoordinateEntry(-104, 618, 390),

        // Target Field BLUE
        nodeCoordinateEntry(-105, 1105, 592),
        nodeCoordinateEntry(-106, 1014, 592),
        nodeCoordinateEntry(-107, 923, 592),
        nodeCoordinateEntry(-108, 832, 592),

        // Target Field Green
        nodeCoordinateEntry(-109, 618, 1070),
        nodeCoordinateEntry(-110, 618, 980),
        nodeCoordinateEntry(-111, 618, 892),
        nodeCoordinateEntry(-112, 618, 800),

        // Target Field Yellow
        nodeCoordinateEntry(-113, 136, 592),
        nodeCoordinateEntry(-114, 226, 592),
        nodeCoordinateEntry(-115, 318, 592),
        nodeCoordinateEntry(-116, 409, 592),
    ]);

    public static isStartField(fieldId: number): boolean {
        return fieldId === 1 || fieldId === 15 || fieldId === 29 || fieldId === 43;
    }

    public static getActionRadius(fieldId: number, canvasSize: number): number {        
        if(this.isStartField(fieldId)) {
            return (75 / 2 * this.getScalingRatio(canvasSize)) + 6;
        }

        return (25 * this.getScalingRatio(canvasSize)) + 6;
    }

    public static isTargetField(fieldId: number): boolean {
        return fieldId <= -101;
    }

    public static getStartFieldIdForColor(color: PinColor): number {
        let startFieldId: number;
        
        switch(color) {
            case PinColor.RED:
              startFieldId = 1;
              break;
            case PinColor.BLUE:
                startFieldId = 15;
                break;
            case PinColor.GREEN:
                startFieldId = 29;
                break;
            case PinColor.YELLOW:
                startFieldId = 43;
                break;
          }

          return startFieldId;
    }

    public static getScaledFields(canvasSize: number): Map<number, Coordinate> {
        const scaleRatio = canvasSize / this.BaseScreenSize;
        const result: Map<number, Coordinate> = new Map<number, Coordinate>();

        this.FIELDS.forEach(({ x, y }, key) => {
            const coordinate: Coordinate = {
                x: x * scaleRatio,
                y: y * scaleRatio
            };

            result.set(key, coordinate);
        });

        return result;
    }

    public static getScalingRatio(canvasSize: number): number {
        return canvasSize / this.BaseScreenSize;
    }
}

// 1316 x 1316
// RED -> 462, 5
// F01 -> 550, 5
// F02 -> 628, 5
// F03 -> 705, 5
// F04 -> 783, 5
// F05 -> 783, 83
// F06 -> 783, 161
// F07 -> 783, 239
// F08 -> 783, 317
// F09 -> 833, 376
// F10 -> 894, 432
// F11 -> 972, 432
// F12 -> 1050, 432
// F13 -> 1128, 432
// BLUE S02 -> 1215, 435
// F14 -> 1215, 518
// F15 -> 1215, 596
// F16 -> 1215, 674
// F17 -> 1215, 752
// F18 -> 1137, 752
// F19 -> 1059, 752
// F20 -> 981, 752
// F21 -> 903, 752
// F22 -> 843, 802
// F23 -> 786, 863
// F24 -> 786, 942
// F25 -> 786, 1019
// F26 -> 786, 1097
// GREEN -> 785, 1180
// F27 -> 699, 1179
// F28 -> 621, 1179
// F29 -> 544, 1179
// F30 -> 466, 1179
// F31 -> 466, 1101
// F32 -> 466, 1023
// F33 -> 466, 945
// F34 -> 466, 867
// F35 -> 415, 805
// F36 -> 354, 750
// F37 -> 276, 750
// F38 -> 198, 750
// F39 -> 120, 750
// YELLOW -> 30, 760
// F40 -> 29, 663
// F41 -> 29, 585
// F42 -> 29, 507
// F43 -> 29, 429
// F44 -> 107, 429
// F45 -> 185, 429
// F46 -> 263, 429
// F47 -> 341, 429
// F48 -> 401, 380
// F49 -> 458, 319
// F50 -> 458, 241
// F51 -> 458, 164
// F52 -> 458, 85

// ==================

// RED Home 01 -> 196, 2
// RED Home 02 -> 114, 2
// RED Home 03 -> 30, 2
// RED Home 04 -> 30, 80

// BLUE Home 01 -> 1205, 168
// BLUE Home 02 -> 1205, 85
// BLUE Home 03 -> 1205, 0
// BLUE Home 04 -> 1128, 0

// GREEN Home 01 -> 1043, 1192
// GREEN Home 02 -> 1127, 1192
// GREEN Home 03 -> 1210, 1192
// GREEN Home 04 -> 1210, 1112

// YELLOW Home 01 -> 23, 1037
// YELLOW Home 02 -> 23, 1120
// YELLOW Home 03 -> 23, 1202
// YELLOW Home 04 -> 102, 1202

// ==================

// RED Goal 01 -> 618, 112
// RED Goal 02 -> 618, 205
// RED Goal 03 -> 618, 298
// RED Goal 04 -> 618, 390

// BLUE Goal 01 -> 1105, 592
// BLUE Goal 02 -> 1014, 592
// BLUE Goal 03 -> 923, 592
// BLUE Goal 04 -> 832, 592

// GREEN Goal 01 -> 618, 1070
// GREEN Goal 02 -> 618, 980
// GREEN Goal 03 -> 618, 892
// GREEN Goal 04 -> 618, 800

// YELLOW Goal 01 -> 136, 592
// YELLOW Goal 02 -> 226, 592
// YELLOW Goal 03 -> 318, 592
// YELLOW Goal 04 -> 409, 592