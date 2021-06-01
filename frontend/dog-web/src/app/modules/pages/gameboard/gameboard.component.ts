import { AfterViewInit, Component, ElementRef, HostListener, OnInit, ViewChild } from '@angular/core';
import { GameBoardRenderer } from 'src/app/models/game/gameboard-renderer';
import { Pin, PinColor } from 'src/app/models/http/dto/pin';
import { FieldUtils } from 'src/app/models/http/fields';
import { GameService } from 'src/app/provider/game.service';

@Component({
  selector: 'app-gameboard',
  templateUrl: './gameboard.component.html',
  styleUrls: ['./gameboard.component.styl']
})
export class GameboardComponent implements OnInit, AfterViewInit {
  public gameBoardImg = new Image();
  public pinImg = new Image();

  @ViewChild('boardContainer')
  public boardContainerRef: ElementRef;

  @ViewChild('board')
  public boardRef: ElementRef;

  public renderer: GameBoardRenderer;

  constructor(public gameService: GameService) {
    
  }

  public ngOnInit(): void {
    
  }

  public ngAfterViewInit(): void {
    this.initCanvasImages();

    this.initRenderer();
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
      [
        new Pin('1', PinColor.BLUE, 1),
        new Pin('2', PinColor.GREEN, 30)
      ],
      FieldUtils.getScaledFields(this.canvasSize)
    );
  }

  private get canvasSize(): number {
    return (window.innerHeight - 50);
  }
}
