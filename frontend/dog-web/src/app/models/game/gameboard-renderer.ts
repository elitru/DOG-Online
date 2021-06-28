import { createPipeType } from "@angular/compiler/src/render3/r3_pipe_compiler";
import { BehaviorSubject, Observable } from "rxjs";
import { CardService } from "src/app/provider/card.service";
import { GameService } from "src/app/provider/game.service";
import { InteractionState } from "../game-state";
import { Coordinate, FieldUtils } from "../http/fields";
import { MovePinMessage } from "../websockets/move-pin-message";
import { CardType } from "./card-type";
import { Pin, PinColor } from "./pin";

export interface RenderQueueItem {
    pin: Pin;
    targetFieldId: number;
    direction: 'forward' | 'backward';
}

export class GameBoardRenderer {
    private static eventSet: (event: MouseEvent) => void | null = null;

    private ctx: CanvasRenderingContext2D;
    private images: HTMLImageElement[] = [];
    private pins: Pin[] = [];
    private actionFields: Map<number, Coordinate> = new Map<number, Coordinate>();
    private actions: number[] = [];
    private selectedPinFields: number[] = [];
    private _clickedFields$: BehaviorSubject<number> = new BehaviorSubject<number>(0);
    private _selectedPin$: BehaviorSubject<Pin> = new BehaviorSubject<Pin>(null);
    public selectedPin: Pin;
    public swapPin: Pin;
    public possibleMoves: number[] = [];
    public pinForMove: Pin;
    public renderQueue: RenderQueueItem[] = [];

    private isRequesting: boolean = false;

    constructor(
        private canvasSize: number,
        private canvas: HTMLCanvasElement,
        private boardImage: HTMLImageElement,
        private userPins: Map<string, Pin[]>,
        private fields: Map<number, Coordinate>,
        private gameService: GameService,
        private cardService: CardService
    ) {
        this.ctx = canvas.getContext('2d');
        
        this.canvas.addEventListener('click', this.onClickCanvas.bind(this));
        GameBoardRenderer.eventSet = this.onClickCanvas.bind(this);

        const scalingRatio = FieldUtils.getScalingRatio(canvasSize);
        
        this.userPins.forEach((value) => value.forEach(p => this.pins.push(p)));

        this.images = [boardImage, ...this.pins.map(p => {
            p.image.width = 80 * scalingRatio;
            p.image.height = 80 * scalingRatio;
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
            //this.movePinOnBoard(this.pins[0], 20, 'forward');
            //this.movePinToTarget(this.pins[0], -107, 'forward');
            //this.movePinToHome(this.pins[0], -1);
            //this.movePinFromHomeToStart(this.pins[0])
        }, 1000);

        this.gameService.movePins$.subscribe(message => this.onMovePin(message));

        this.gameService.interactionState$.subscribe(state => {
            if(state !== InteractionState.SelectMove && this.possibleMoves.length === 1) {
                //this.makeMove(this.pinForMove, this.possibleMoves[0]);
            }
        });
    }

    public resizeRenderer(canvasSize: number): void {
        this.canvasSize = canvasSize;
        const scalingRatio = FieldUtils.getScalingRatio(this.canvasSize);
        
        this.userPins.forEach((value) => value.forEach(p => this.pins.push(p)));

        this.images = [this.boardImage, ...this.pins.map(p => {
            p.image.width = 80 * scalingRatio;
            p.image.height = 80 * scalingRatio;
            return p.image;
        })];

        this.pins.forEach(p => p.coordinates = this.fields.get(p.fieldId));

        this.setCanvasDimensions();
        this.waitForAllImagesToBeLoadedAndInitialize();

        this.actionFields = new Map();
        this.initFields();
    }

    private async makeMove(pin: Pin, targetField: number): Promise<void> {        
        if(!pin) return;
        
        this.actions = [];
        const cardType = this.cardService.selectedCard.type === CardType.Joker ? this.cardService.jokerAction : this.cardService.selectedCard.type;
                        
        switch(cardType) {
            case CardType.Two:
            case CardType.Three:
            case CardType.Five:
            case CardType.Six:
            case CardType.Eight:
            case CardType.Nine:
            case CardType.Ten:
            case CardType.Twelve:
            case CardType.StartEleven:
            case CardType.StartThirteen:
            case CardType.PM_Four:
                await this.gameService.makeSimpleMove(this.cardService.selectedCard.id, pin.pinId, targetField, this.cardService.jokerAction);
                break;
        }
    }

    private async onMovePin(message: MovePinMessage): Promise<void> {
        if(!message) return;

        const { pinId, targetFieldId, direction } = message;
        const pin = this.pins.find(p => p.pinId === pinId);

        if(pin.fieldId < 0 && pin.fieldId >= -16) {
            this.movePinFromHomeToStart(pin);
        }else if(targetFieldId > 0) {
            this.movePinOnBoard(pin, targetFieldId, direction);
        }else if(targetFieldId < 0 && targetFieldId >= -16) {
            setTimeout(async () => await this.movePinToHome(pin, targetFieldId), 100)
        }else{
            this.movePinToTarget(pin, targetFieldId, direction);
        }
    }

    public get selectedPin$(): Observable<Pin> {
        return this._selectedPin$;
    }

    private isUserPin(pinId: string): boolean {
        return !this.userPins.get(this.gameService.self.id).every(pin => pin.pinId !== pinId);
    }

    public clearSelectedPins(): void {
        this.selectedPin = null;
        this.swapPin = null;
        this.selectedPinFields = [];
    }

    private reset(): void {
        this.selectedPinFields = [];
        this.selectedPin = null;
        this.swapPin = null;
        this.gameService.removeCardFromStack(this.cardService.selectedCard.id);
        this.cardService.selectedCard = null;
        this.cardService.jokerAction = null;
        this.pinForMove = null;
        this.possibleMoves = [];
        this.initializeBoard();
    }

    private async onSelectField(fieldId: number) {
        const pin = this.getPinForField(fieldId);
        
        const currentState = this.gameService.interactionState$.getValue();
        /*console.log('curr field -> ' + fieldId);
        console.log('selected pin -> ' + pin);
        console.log(currentState);
        console.log('selected -> ' + this.selectedPin);
        console.log(this.actions);
        console.log(this.isRequesting);
        console.log(this.pinForMove);*/
        

        if(currentState === InteractionState.SelectMove) {
            //console.log(this.actions);
            //console.log(fieldId);
                        
            if(!this.actions.includes(fieldId)) return;
            if(this.isRequesting) return;
            
            this.isRequesting = true;
            await this.makeMove(this.pinForMove, fieldId);
            this.isRequesting = false;
            this.gameService.setInteractionState(InteractionState.NoTurn);
            this.reset();
            return;
        }

        if(currentState === InteractionState.SelectTwoPinsForSwap && pin) {
            this.selectedPinFields.push(fieldId);
            if(!this.selectedPin) {
                this.selectedPin = pin;
            }else{
                this.swapPin = pin;
                await this.gameService.swapPins(this.cardService.selectedCard, this.selectedPin.pinId, this.swapPin.pinId);
                this.reset();
            }

            return;
        }

        if(currentState === InteractionState.SelectPin && pin && this.isUserPin(pin.pinId) && !this.selectedPin) {
            this.selectedPin = pin;
            //console.log('fired...');
            this._selectedPin$.next(pin);
            return;
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
        this.actions.forEach(fieldId => this.renderActionsOnField(fieldId, true));
        this.selectedPinFields.forEach(fieldId => this.renderActionsOnField(fieldId, true, false));
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

    private async movePinToHome(pin: Pin, targetHomeFieldId: number): Promise<void> {
        const coordinate = FieldUtils.getScaledFields(this.canvasSize).get(targetHomeFieldId);
        await this.animateFromTo(pin, targetHomeFieldId, coordinate);
    }

    private async movePinOnBoard(pin: Pin, toFieldId: number, direction: 'forward' | 'backward'): Promise<void> {
        if(!pin || pin.fieldId === toFieldId) return;

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

    public async movePinFromHomeToStart(pin: Pin): Promise<void>  {
        const { color } = pin;
        const startFieldId: number = FieldUtils.getStartFieldIdForColor(color);
        const coords: Coordinate = this.fields.get(startFieldId);
        
        await this.animateFromTo(pin, startFieldId, coords);
    }

    private animateFromTo(pin: Pin, nextFieldId: number, to: Coordinate): Promise<void> {
        if(!to) return;
        
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
            if(!pin.selectable) {
                this.ctx.globalAlpha = .5;
            }

            this.drawNinePin(pin.image, pin.coordinates.x, pin.coordinates.y);
            this.ctx.globalAlpha = 1;
        })
    }

    private drawNinePin(image: HTMLImageElement, x: number, y: number): void {        
        this.ctx.drawImage(image, x, y, image.width, image.width);
    }

    public setActionField(fieldIds: number | number[]): void {
        if(typeof(fieldIds) === 'number') {
            this.renderActionsOnField(fieldIds);
            return;
        }

        fieldIds.forEach(id => this.renderActionsOnField(id));
    }

    private renderActionsOnField(fieldId: number, justRender: boolean = false, withImage: boolean = true): void {
        const radius = FieldUtils.getActionRadius(fieldId, this.canvasSize);
        const { x, y } = this.fields.get(fieldId);

        this.ctx.fillStyle = '#da7000';
        this.ctx.strokeStyle = '#fff';
        this.ctx.lineWidth = 10;
        this.ctx.beginPath();
        
        if(!this.actions.includes(fieldId) && !justRender) {
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
        
        if(!withImage) return;

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