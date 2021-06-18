export interface MovePinMessageDTO {
    pinId: string;
    targetFieldId: number;
    direction?: 'forward' | 'backward';
}