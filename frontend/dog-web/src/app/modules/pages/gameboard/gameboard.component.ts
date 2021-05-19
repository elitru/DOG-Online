import { AfterViewInit, Component, ElementRef, HostListener, OnInit, ViewChild } from '@angular/core';

@Component({
  selector: 'app-gameboard',
  templateUrl: './gameboard.component.html',
  styleUrls: ['./gameboard.component.styl']
})
export class GameboardComponent implements OnInit, AfterViewInit {
  @ViewChild('boardContainer')
  public boardContainerRef: ElementRef;

  @ViewChild('board')
  public boardRef: ElementRef;

  constructor() { }

  public ngOnInit(): void {
    
  }

  public ngAfterViewInit(): void {
    this.setCanvasDimensions();
  }

  @HostListener('window:resize', ['$event'])
  public onResize(event) {
    if(!this.boardContainerRef || !this.boardRef) return;
    
    this.setCanvasDimensions();
  }

  private setCanvasDimensions(): void {

    this.boardRef.nativeElement.style.width = (window.innerHeight - 50) + 'px';
    this.boardRef.nativeElement.style.height = (window.innerHeight - 50) + 'px';
  }
}
