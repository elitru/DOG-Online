import { AfterViewInit, Component, ElementRef, HostListener, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { Subscription } from 'rxjs';
import { GameState, InteractionState } from 'src/app/models/game-state';
import { CardType } from 'src/app/models/game/card-type';
import { GameBoardRenderer } from 'src/app/models/game/gameboard-renderer';
import { Messages } from 'src/app/models/game/message';
import { Pin, PinColor } from 'src/app/models/game/pin';
import { FieldUtils } from 'src/app/models/http/fields';
import { CardService } from 'src/app/provider/card.service';
import { DialogService } from 'src/app/provider/dialog.service';
import { GameService } from 'src/app/provider/game.service';
import { LoaderService } from 'src/app/provider/loader.service';

@Component({
  selector: 'app-gameboard',
  templateUrl: './gameboard.component.html',
  styleUrls: ['./gameboard.component.styl']
})
export class GameboardComponent implements OnInit, AfterViewInit, OnDestroy {
  public gameBoardImg = new Image();

  private isLoading: boolean = false;
  
  private stateSubscription: Subscription;

  @ViewChild('boardContainer')
  public boardContainerRef: ElementRef;

  @ViewChild('board')
  public boardRef: ElementRef;

  public renderer: GameBoardRenderer;

  constructor(public gameService: GameService,
              public loaderService: LoaderService,
              public cardService: CardService,
              public dialogService: DialogService) {
    this.loaderService.setLoading(true);
  }

  public ngOnInit(): void {
    
  }

  public ngOnDestroy(): void {
    this.stateSubscription.unsubscribe();
  }

  public ngAfterViewInit(): void {
    this.initCanvasImages();
    this.initRenderer();

    setTimeout(() => this.initRenderer(), 200);
  }

  private async getMoves(pin: Pin): Promise<void> {    
    if(this.isLoading || !this.cardService.selectedCard) return;

    try {
      this.isLoading = true;
      const fieldIds = await this.gameService.getMoves(this.cardService.selectedCard.id, pin.pinId, this.cardService.jokerAction);
      
      if(fieldIds.length === 0) {
        this.dialogService.show('Fehler', Messages.NO_MOVE_POSSIBLE);
        this.gameService.setInteractionState(InteractionState.SelectPin);
        this.isLoading = false;
        return;
      }

      this.renderer.possibleMoves = fieldIds;
      this.renderer.pinForMove = pin;
      
      this.renderer.selectedPin = pin;
      this.renderer.setActionField(fieldIds);
      this.gameService.setInteractionState(InteractionState.SelectMove);
      this.isLoading = false;
    }catch(e) {
      console.log('error >');
      console.log(e);
      this.isLoading = false;
    }
  }

  private initCanvasImages(): void {
    this.gameBoardImg.src = '/assets/board.svg';
    this.gameBoardImg.width = this.canvasSize;
    this.gameBoardImg.height = this.canvasSize;
  }

  @HostListener('window:resize', ['$event'])
  public onResize(event) {
    if(!this.boardContainerRef || !this.boardRef) return;
    
    this.initRenderer();
  }

  private initRenderer(): void {
    if(!this.renderer) {
      this.renderer = new GameBoardRenderer(
        // canvas size
        this.canvasSize,
        // html canvas element
        this.boardRef.nativeElement,
        // game board image element
        this.gameBoardImg,
        // test pin
        this.gameService.pins,
        FieldUtils.getScaledFields(this.canvasSize),
        this.gameService,
        this.cardService
      );
    }else{
      this.renderer.resizeRenderer(this.canvasSize);
    }

    this.loaderService.setLoading(false);

    this.renderer.selectedPin$.subscribe(pin => {
      if(!pin) return;

      // make api call to get available moves
      this.getMoves(pin);
    });
  }

  private get canvasSize(): number {
    return (window.innerHeight - 50);
  }
}
