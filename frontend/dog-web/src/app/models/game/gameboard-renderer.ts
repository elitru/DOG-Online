import { createPipeType } from "@angular/compiler/src/render3/r3_pipe_compiler";
import { BehaviorSubject, Observable } from "rxjs";
import { CardService } from "src/app/provider/card.service";
import { GameService } from "src/app/provider/game.service";
import { InteractionState } from "../game-state";
import { Coordinate, FieldUtils } from "../http/fields";
import { Pin, PinColor } from "./pin";

export class GameBoardRenderer {
    private ctx: CanvasRenderingContext2D;
    private images: HTMLImageElement[] = [];
    private pins: Pin[] = [];
    private actionFields: Map<number, Coordinate> = new Map<number, Coordinate>();
    private actions: number[] = [];
    private _clickedFields$: BehaviorSubject<number> = new BehaviorSubject<number>(0);
    private _selectedPin$: BehaviorSubject<Pin> = new BehaviorSubject<Pin>(null);
    public selectedPin: Pin;

    constructor(
        private readonly canvasSize: number,
        private canvas: HTMLCanvasElement,
        private readonly boardImage: HTMLImageElement,
        private userPins: Map<string, Pin[]>,
        private readonly fields: Map<number, Coordinate>,
        private gameService: GameService,
        private cardService: CardService
    ) {         
        this.ctx = canvas.getContext('2d');
        this.canvas.addEventListener('click', this.onClickCanvas.bind(this));

        const scalingRatio = FieldUtils.getScalingRatio(canvasSize);
        
        this.userPins.forEach((value) => value.forEach(p => this.pins.push(p)));

        this.images = [boardImage, ...this.pins.map(p => {
            p.image.width = p.image.width * scalingRatio;
            p.image.height = p.image.height * scalingRatio;
            return p.image;
        })];

        this.pins.forEach(p => p.coordinates = fields.get(p.fieldId));

        this.setCanvasDimensions();
        this.waitForAllImagesToBeLoadedAndInitialize();

        this._clickedFields$.subscribe(fieldId => {
            if(!fieldId) return;
            
            this.onSelectField(fieldId);
        });

        this.initFields();

        setTimeout(() => {
            //this.movePinOnBoard(this.pins[0], 45, 'forward');
            //this.movePinToTarget(this.pins[0], -107, 'forward');
            //this.movePinToHome(this.pins[0]);
        }, 1000); 
    }

    public get selectedPin$(): Observable<Pin> {
        return this._selectedPin$;
    }

    private isUserPin(pinId: string): boolean {
        return !this.userPins.get(this.gameService.self.id).every(pin => pin.pinId !== pinId);
    }

    private async onSelectField(fieldId: number) {
        const pin = this.getPinForField(fieldId);

        const currentState = this.gameService.interactionState$.getValue();

        if(currentState === InteractionState.SelectPin && pin && this.isUserPin(pin.pinId)) {
            this._selectedPin$.next(pin);
            return;
        }

        if(currentState === InteractionState.SelectMove) {
            await this.gameService.makeMove(this.selectedPin.pinId, this.cardService.selectedCard, fieldId, this.cardService.jokerAction);
            this.cardService.selectedCard = null;
            this.cardService.jokerAction = null;
            this.selectedPin = null;
            this.gameService.setInteractionState(InteractionState.NoTurn);
        }
    }

    private initFields(): void {
        this.fields.forEach(({ x, y }, fieldId) => {
            const radius = FieldUtils.getActionRadius(fieldId, this.canvasSize);

            if(!FieldUtils.isStartField(fieldId)) {
                this.ctx.arc(x + radius + 8, y + radius * 2 + 2, radius, 0, 2 * Math.PI);
                this.actionFields.set(fieldId, {
                    x: x + radius + 6,
                    y: y + radius * 2 + 2
                });
            }else{
                this.ctx.arc(x + radius - 2, y + radius + 17, radius, 0, 2 * Math.PI);
                this.actionFields.set(fieldId, {
                    x: x + radius - 4,
                    y: y + radius + 17
                });
            }
        });
    }

    private getPinForField(field: number): Pin | null {
        const pin = this.pins.find(({fieldId}: Pin) => fieldId === field);
        return pin || null;
    }

    private async waitForAllImagesToBeLoadedAndInitialize(): Promise<void> {
        if(this.images.every(img => img.complete && img.naturalHeight !== 0)) {
            this.initializeBoard();
            return;
        }

        let loaded: number = 0;


        this.images.forEach(img => img.onload = () => {
            loaded++;

            if(loaded !== this.images.length) return;

            // render game board
            this.initializeBoard();            
            //this.renderActionsOnField(6);
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

    private isPinOnField(fieldId: number): boolean {
        return !this.pins.every(pin => pin.fieldId !== fieldId);
    }

    private getPreviousFieldId(currentFieldId: number): number {
        if(currentFieldId === 1) {
            return FieldUtils.AmountOfGameFields;
        }

        return currentFieldId - 1;
    }

    private getStartFieldForPin(pin: Pin): number {
        switch(pin.color) {
            case PinColor.RED:      return 1;
            case PinColor.BLUE:     return 15;
            case PinColor.GREEN:    return 29;
            case PinColor.YELLOW:   return 43;
        }
    }

    private getFirstTargetFieldForPin(pin: Pin): number {
        switch(pin.color) {
            case PinColor.RED:      return -101;
            case PinColor.BLUE:     return -105;
            case PinColor.GREEN:    return -109;
            case PinColor.YELLOW:   return -113;
        }
    }

    private getNextFreeHomeField(pin: Pin): number {
        let firstFreeHomeField: number = 0;

        switch(pin.color) {
            case PinColor.RED:      firstFreeHomeField = -1;     break;
            case PinColor.BLUE:     firstFreeHomeField = -5;     break;
            case PinColor.GREEN:    firstFreeHomeField = -9;     break;
            case PinColor.YELLOW:   firstFreeHomeField = -13;    break;
        }

        while(this.isPinOnField(firstFreeHomeField)) {            
            firstFreeHomeField--;
        }

        return firstFreeHomeField;
    }

    private async movePinToTarget(pin: Pin, targetFieldId: number, direction: 'forward' | 'backward'): Promise<void> {
        // move pin to start field
        if(targetFieldId > 0) {
            const startFieldId = this.getStartFieldForPin(pin);
            await this.movePinOnBoard(pin, startFieldId, direction);
        }

        // move to target field
        let nextTargetField = this.getFirstTargetFieldForPin(pin);

        while(pin.fieldId !== targetFieldId) {
            const coordinate = this.fields.get(nextTargetField);
            await this.animateFromTo(pin, nextTargetField, coordinate);
            nextTargetField--;
        }
    }

    private async movePinToHome(pin: Pin): Promise<void> {
        const firstFreeHomeField = this.getNextFreeHomeField(pin);
        const coordinate = this.fields.get(firstFreeHomeField);
        
        await this.animateFromTo(pin, firstFreeHomeField, coordinate);
    }

    private async movePinOnBoard(pin: Pin, toFieldId: number, direction: 'forward' | 'backward'): Promise<void> {
        if(pin.fieldId === toFieldId) return;

        if(direction === 'forward') {
            // animate pin forward
            // move pin to next field
            const nextFieldId = this.getNextFieldId(pin.fieldId);
            const nextCoords: Coordinate = this.fields.get(nextFieldId);

            await this.animateFromTo(pin, nextFieldId, nextCoords);
            await this.movePinOnBoard(pin, toFieldId, direction);
        }else{
            // animate pin backward
            const previousFieldId = this.getPreviousFieldId(pin.fieldId);
            const previousCoords: Coordinate = this.fields.get(previousFieldId);

            await this.animateFromTo(pin, previousFieldId, previousCoords);
            await this.movePinOnBoard(pin, toFieldId, direction);
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

    public setActionField(fieldIds: number | number[]): void {
        this.actions = [];

        if(typeof(fieldIds) === 'number') {
            this.renderActionsOnField(fieldIds);
            return;
        }

        fieldIds.forEach(id => this.renderActionsOnField(id));
    }

    private renderActionsOnField(fieldId: number): void {
        const radius = FieldUtils.getActionRadius(fieldId, this.canvasSize);
        const { x, y } = this.fields.get(fieldId);

        this.ctx.fillStyle = '#da7000';
        this.ctx.strokeStyle = '#fff';
        this.ctx.lineWidth = 10;
        this.ctx.beginPath();
        
        if(!this.actions.includes(fieldId)) {
            this.actions.push(fieldId);
        }

        if(!FieldUtils.isStartField(fieldId)) {
            this.ctx.arc(x + radius + 8, y + radius * 2 + 2, radius, 0, 2 * Math.PI);
        }else{
            this.ctx.arc(x + radius - 2, y + radius + 17, radius, 0, 2 * Math.PI);
        }

        this.ctx.stroke();
        this.ctx.fill();
        this.ctx.font = `18pt Nunito`;
        this.ctx.fillStyle = '#000';
        
        if(FieldUtils.isTargetField(fieldId)) {
            this.ctx.fillText('👑', x + radius - 11, y + radius * 2 + 10);
        }else{
            if(FieldUtils.isStartField(fieldId)) {
                this.ctx.fillText('📍', x + radius - 12, y + radius + 25);
            }else{
                this.ctx.fillText('📍', x + radius - 3, y + radius * 2 + 10);
            }
        }
    }

    private getClickCoordinates(x: number, y: number): Coordinate {
        const rect = this.canvas.getBoundingClientRect();

        return {
            x: x - rect.left,
            y: y - rect.top
        };
    }

    private onClickCanvas(event: MouseEvent): void {
        const clickCoordinates = this.getClickCoordinates(event.clientX, event.clientY);
        const fieldId = this.getFieldForClick(clickCoordinates);
        if(!fieldId) return;

        if(this.actions.includes(fieldId)) {
            // clear actions
            this.actions = [];
        }

        this.initializeBoard();

        this._clickedFields$.next(fieldId);
    }

    private getFieldForClick({ x, y }: Coordinate): number | null {
        let result: number = -1;
        
        this.actionFields.forEach((fieldCoords, fieldId) => {
            const actionFieldRadius = FieldUtils.getActionRadius(fieldId, this.canvasSize);

            if(
                // check x coords
                x >= (fieldCoords.x - actionFieldRadius) && x <= (fieldCoords.x + actionFieldRadius)
                    &&
                // check y coords
                y >= (fieldCoords.y - actionFieldRadius) && y <= (fieldCoords.y + actionFieldRadius)
            ) {
                // action found
                result = fieldId;
            }
        });

        return result === -1 ? null : result;
    }
}