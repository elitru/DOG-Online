import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { InteractionState } from 'src/app/models/game-state';
import { Card } from 'src/app/models/game/card';
import { CardType } from 'src/app/models/game/card-type';
import { CardService } from 'src/app/provider/card.service';
import { GameService } from 'src/app/provider/game.service';

@Component({
  selector: 'app-card',
  templateUrl: './card.component.html',
  styleUrls: ['./card.component.styl']
})
export class CardComponent implements OnInit {

  @Input()
  public card: Card;

  constructor(public cardService: CardService,
              public gameService: GameService) { }

  public ngOnInit(): void {
    
  }

  public get imageUrl(): string {
    return `/assets/cards/${this.card.type}.svg`;
  }

  public async onSelect(): Promise<void> {
    if(!this.cardService.isSelectable) return;

    this.cardService.select(this.card);

    const currentState = this.gameService.interactionState$.getValue();
    console.log('state -> ' + currentState);
    console.log(this.card.type);
    
    if(currentState === InteractionState.SwapCardWithTeamMate) {
      const team = this.gameService.getTeamForPlayer(this.gameService.self.id);
      const teamMate = team.members.filter(p => p.id !== this.gameService.self.id)[0];
      
      await this.gameService.swapCard(this.cardService.selectedCard.id, teamMate.id);
      this.cardService.select(null);
    }else if(currentState === InteractionState.SelectCardForMove) {
      switch(this.card.type) {
        case CardType.StartEleven:
        case CardType.StartThirteen:
          this.gameService.setInteractionState(InteractionState.SelectCardAction);
          break;

        case CardType.Joker:
          console.log('is joker');
          this.gameService.setInteractionState(InteractionState.SelectJokerAction);
          break;

        default:
          this.gameService.setInteractionState(InteractionState.SelectPin);
          break;
      }
    }
  }
}
