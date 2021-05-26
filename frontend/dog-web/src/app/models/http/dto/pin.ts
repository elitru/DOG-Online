import { Coordinate } from "../fields";

export enum PinColor {
    RED = 'red.svg',
    BLUE = 'blue.svg',
    GREEN = 'green.svg',
    YELLOW = 'yellow.svg'
}

export class Pin {
    public image: HTMLImageElement;

    constructor(
        public readonly pinId: string,
        public readonly color: PinColor,
        public fieldId: number,
        public coordinates?: Coordinate
    ) {
        this.image = new Image();
        this.image.src = '/assets/pins/' + this.color;
        this.image.width = 80;
        this.image.height = 80;
    }
}