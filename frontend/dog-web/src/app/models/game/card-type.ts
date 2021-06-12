export enum CardType {
    Two = '2',
    Three = '3',
    Five = '5',
    Six = '6',
    Seven = '7',
    Eight = '8',
    Nine = '9',
    Ten = '10',
    Twelve = '12',
    Joker = 'joker',
    Swap = 'swap',
    StartEleven = '1_11',
    StartThirteen = '1_13',
    PM_Four = '4',
}

export const MoveCards: CardType[] = [
    CardType.Two,
    CardType.Three,
    CardType.Five,
    CardType.Six,
    CardType.Seven,
    CardType.Eight,
    CardType.Nine,
    CardType.Ten,
    CardType.Twelve,
    CardType.PM_Four,
    CardType.Swap,
    CardType.StartEleven,
    CardType.StartThirteen
]