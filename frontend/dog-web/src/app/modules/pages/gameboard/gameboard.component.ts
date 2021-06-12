import { AfterViewInit, Component, ElementRef, HostListener, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { Subscription } from 'rxjs';
import { GameState, InteractionState } from 'src/app/models/game-state';
import { GameBoardRenderer } from 'src/app/models/game/gameboard-renderer';
import { Pin, PinColor } from 'src/app/models/game/pin';
import { FieldUtils } from 'src/app/models/http/fields';
import { CardService } from 'src/app/provider/card.service';
import { GameService } from 'src/app/provider/game.service';
import { LoaderService } from 'src/app/provider/loader.service';

@Component({
  selector: 'app-gameboard',
  templateUrl: './gameboard.component.html',
  styleUrls: ['./gameboard.component.styl']
})
export class GameboardComponent implements OnInit, AfterViewInit, OnDestroy {
  public gameBoardImg = new Image();
  public pinImg = new Image();
  
  private stateSubscription: Subscription;

  @ViewChild('boardContainer')
  public boardContainerRef: ElementRef;

  @ViewChild('board')
  public boardRef: ElementRef;

  public renderer: GameBoardRenderer;

  constructor(public gameService: GameService,
              public loaderService: LoaderService,
              public cardService: CardService) {
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

    this.renderer.selectedPin$.subscribe(pin => {
      if(!pin) return;

      // make api call to get available moves
      this.getMoves(pin);
    });
  }

  private async getMoves(pin: Pin): Promise<void> {
    const fieldIds = await this.gameService.getAvailableMoves(pin.pinId, this.cardService.selectedCard, this.cardService.jokerAction);
    this.renderer.setActionField(fieldIds);
    this.gameService.setInteractionState(InteractionState.SelectMove);
  }

  private initCanvasImages(): void {
    this.gameBoardImg.src = '/assets/board.svg';
    this.gameBoardImg.width = this.canvasSize;
    this.gameBoardImg.height = this.canvasSize;

    this.pinImg.src = '/assets/pins/red.svg';
    this.pinImg.width = 80;
    this.pinImg.height = 80;
  }

  @HostListener('window:resize', ['$event'])
  public onResize(event) {
    if(!this.boardContainerRef || !this.boardRef) return;
    
    this.initRenderer();
  }

  private initRenderer(): void {
    this.renderer = new GameBoardRenderer(
      // canvas size
      (window.innerHeight - 50),
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

    this.loaderService.setLoading(false);
  }

  private get canvasSize(): number {
    return (window.innerHeight - 50);
  }
}
