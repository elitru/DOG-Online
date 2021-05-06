import { MessageDTO } from "./message.dto";

export interface StateChangedMessageDTO<TData> extends MessageDTO {
    data: TData
    next: string;
};