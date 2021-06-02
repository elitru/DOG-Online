import { PinDTO } from "../http/dto/pin.dto";
import { Coordinate } from "../http/fields";

export enum PinColor {
    RED = 'red.svg',
    BLUE = 'blue.svg',
    GREEN = 'green.svg',
    YELLOW = 'yellow.svg'
}

const mapColor = (color: 'green' | 'yellow' | 'blue' | 'red'): PinColor => {
    switch(color) {
        case 'blue': return PinColor.BLUE;
        case 'yellow': return PinColor.YELLOW;
        case 'red': return PinColor.RED;
        case 'green': return PinColor.GREEN;
    }
};

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

    public static fromApi(dto: PinDTO): Pin {
        return new Pin(dto.pinId, mapColor(dto.color), dto.fieldId);
    }
}