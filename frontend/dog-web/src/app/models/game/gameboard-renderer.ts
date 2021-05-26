export class GameBoardRenderer {
    private ctx: CanvasRenderingContext2D;
    private images: HTMLImageElement[] = [];

    constructor(
        private readonly canvasSize: number,
        private canvas: HTMLCanvasElement,
        private readonly boardImage: HTMLImageElement,
        private readonly pin: HTMLImageElement
    ) { 
        this.ctx = canvas.getContext('2d');        

        this.images = [boardImage, pin];

        this.setCanvasDimensions();
        this.waitForAllImagesToBeLoadedAndInitialize();
    }

    private async waitForAllImagesToBeLoadedAndInitialize(): Promise<void> {
        let loaded: number = 0;

        this.images.forEach(img => img.onload = () => {
            loaded++;

            if(loaded !== this.images.length) return;

            // render game board
            this.initializeBoard();
        });
    }

    private setCanvasDimensions(): void {
        this.canvas.width = this.canvasSize * window.devicePixelRatio;
        this.canvas.height = this.canvasSize * window.devicePixelRatio;
        this.canvas.style.width = `${this.canvasSize}px`;
        this.canvas.style.height = `${this.canvasSize}px`;
    }

    private async initializeBoard(): Promise<void> {
        this.ctx.imageSmoothingEnabled = false;

        this.drawBoardImage();
        this.drawNinePin(409, 592);
    }

    private drawBoardImage(): void {
        this.ctx.drawImage(this.boardImage, 0, 0, this.boardImage.width, this.boardImage.width);
    }

    private drawNinePin(x: number, y: number): void {
        this.ctx.drawImage(this.pin, x, y, this.pin.width, this.pin.width);
    }
}

// 1316 x 1316
// RED S01 -> 462, 5
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

// RED Start 01 -> 196, 2
// RED Start 02 -> 114, 2
// RED Start 03 -> 30, 2
// RED Start 04 -> 30, 80

// BLUE Start 01 -> 1205, 168
// BLUE Start 02 -> 1205, 85
// BLUE Start 03 -> 1205, 0
// BLUE Start 04 -> 1128, 0

// GREEN Start 01 -> 1043, 1192
// GREEN Start 02 -> 1127, 1192
// GREEN Start 03 -> 1210, 1192
// GREEN Start 04 -> 1210, 1112

// YELLOW Start 01 -> 23, 1037
// YELLOW Start 02 -> 23, 1120
// YELLOW Start 03 -> 23, 1202
// YELLOW Start 04 -> 102, 1202

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