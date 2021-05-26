import { Pin } from "../http/dto/pin";
import { Coordinate, FieldUtils } from "../http/fields";

export class GameBoardRenderer {
    private ctx: CanvasRenderingContext2D;
    private images: HTMLImageElement[] = [];

    constructor(
        private readonly canvasSize: number,
        private canvas: HTMLCanvasElement,
        private readonly boardImage: HTMLImageElement,
        private readonly pins: Pin[],
        private readonly fields: Map<number, Coordinate>
    ) { 
        this.ctx = canvas.getContext('2d');        

        this.images = [boardImage, ...pins.map(p => p.image)];

        this.pins.forEach(p => p.coordinates = fields.get(p.fieldId));

        this.setCanvasDimensions();
        this.waitForAllImagesToBeLoadedAndInitialize();

        setTimeout(() => {
            this.animatePin(this.pins[0], 45, 'backward');
        }, 1000); 
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
        this.ctx.clearRect(0, 0, this.canvasSize, this.canvasSize);
        this.ctx.imageSmoothingEnabled = false;

        this.drawBoardImage();
        this.renderPins();
    }

    private drawBoardImage(): void {
        this.ctx.drawImage(this.boardImage, 0, 0, this.boardImage.width, this.boardImage.width);
    }

    private getNextFieldId(currentFieldId: number): number {
        const next = (currentFieldId + 1) % (FieldUtils.AmountOfGameFields + 1);
        return next === 0 ? 1 : next;
    }

    private getPreviousFieldId(currentFieldId: number): number {
        if(currentFieldId === 1) {
            return FieldUtils.AmountOfGameFields;
        }

        return currentFieldId - 1;
    }

    private async animatePin(pin: Pin, toFieldId: number, direction: 'forward' | 'backward'): Promise<void> {
        if(pin.fieldId === toFieldId) return;

        if(direction === 'forward') {
            // animate pin forward
            // move pin to next field
            const nextFieldId = this.getNextFieldId(pin.fieldId);
            const nextCoords: Coordinate = this.fields.get(nextFieldId);

            await this.animateFromTo(pin, nextFieldId, nextCoords);
            await this.animatePin(pin, toFieldId, direction);
        }else{
            // animate pin backward
            const previousFieldId = this.getPreviousFieldId(pin.fieldId);
            const previousCoords: Coordinate = this.fields.get(previousFieldId);

            await this.animateFromTo(pin, previousFieldId, previousCoords);
            await this.animatePin(pin, toFieldId, direction);
        }
    }

    private animateFromTo(pin: Pin, nextFieldId: number, to: Coordinate): Promise<void> {
        const movement = 5 * FieldUtils.getScalingRatio(this.canvasSize);
        const promise = new Promise<void>((resolve) => {
            const interval = setInterval(() => {                
                let finished: boolean = false;
                
                if(pin.coordinates.x < to.x) {
                    pin.coordinates.x += movement;

                    if(pin.coordinates.x > to.x) {
                        pin.coordinates.x = to.x;
                    }

                    finished = false;
                }else if(pin.coordinates.x > to.x){
                    pin.coordinates.x -= movement;
                    finished = false;

                    if(pin.coordinates.x < to.x) {
                        pin.coordinates.x = to.x;
                    }
                }else{
                    finished = true;
                }
    
                if(pin.coordinates.y < to.y) {
                    pin.coordinates.y += movement;
                    finished = false;

                    if(pin.coordinates.y > to.y) {
                        pin.coordinates.y = to.y;
                    }
                }else if(pin.coordinates.y > to.y){
                    pin.coordinates.y -= movement;
                    finished = false;

                    if(pin.coordinates.y < to.y) {
                        pin.coordinates.y = to.y;
                    }
                }else{
                    if(finished) {
                        finished = true;
                    }else{
                        finished = false;
                    }
                }                

                if(finished) {
                    pin.fieldId = nextFieldId;
                    pin.coordinates = {...to};
                    resolve();
                    clearInterval(interval);
                }
    
                this.initializeBoard();
            }, 5);
        });

        return promise;
    }

    private renderPins(): void {
        this.pins.forEach(pin => {
            this.drawNinePin(pin.image, pin.coordinates.x, pin.coordinates.y);
        })
    }

    private drawNinePin(image: HTMLImageElement, x: number, y: number): void {
        this.ctx.drawImage(image, x, y, image.width, image.width);
    }
}