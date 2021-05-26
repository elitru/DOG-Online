// import { FieldDTO } from "./dto/field.dto";

// export class Field {
//     constructor(
//         public nodeId: string,
//         public next: Field,
//         public previous: Field
//     ) { }

//     public static fromApi(field: FieldDTO): Field {
//         if(field.firstHomeFieldId && field.firstTargetFieldId) {
//             return new StartField(field.nodeId, null, null, null, null);
//         }

//         return new Field(field.nodeId, null, null);
//     }
// }

// export class StartField extends Field {
//     constructor(
//         nodeId: string,
//         next: Field,
//         previous: Field,
//         public firstHome: Field,
//         public firstTarget: Field
//     ) {
//         super(nodeId, next, previous);
//     }
// }

// export const buildGameBoard = (fields: FieldDTO[]): GameBoard => {
//     const start = Field.fromApi(mocks[0]) as StartField;
//     return new GameBoard();
// }

// const getFieldById = (fields: FieldDTO[], fieldId: string): Field => {
//     return Field.fromApi(fields.find(f => f.nodeId === fieldId)!);
// }

// const buildField = (start: Field, fields: FieldDTO[], current: Field, nextId?: string, isFirstGo: boolean = true): Field => {
//     if(!nextId) return;

//     if(current.nodeId === start.nodeId && !isFirstGo) {
//         const prev = current.previous;
//         prev.next = start;
//         start.previous = prev;
//         return start;
//     }

//     const next = getFieldById(fields, nextId);
//     current.next = next;
//     next.previous = current;
    
//     if(!(current instanceof StartField)) {
//         return buildField(start, fields, next, fields.find(f => f.nodeId === next.nodeId).nextId, false);
//     }
    
//     //call also for home & target fields
//     const currentRaw = fields.find(f => f.nodeId === current.nodeId);
//     const firstHome = getFieldById(fields, currentRaw.firstHomeFieldId);
//     current.firstHome = firstHome;
//     firstHome.previous = current;
//     buildField(start, fields, firstHome, fields.find(f => f.nodeId === firstHome.nodeId).nextId);

//     const firstTarget = getFieldById(fields, currentRaw.firstTargetFieldId);
//     current.firstTarget = firstTarget;
//     firstTarget.previous = current;
//     buildField(start, fields, firstTarget, fields.find(f => f.nodeId === firstTarget.nodeId).nextId);

//     return buildField(start, fields, next, fields.find(f => f.nodeId === next.nodeId).nextId, false);
// }

// const mocks: FieldDTO[] = [
//     {
//        "nodeId":"1372e613-5510-48a0-8b8b-fad3afa92ac1",
//        "previousId":"6557b4c3-a13c-486a-a6f9-7f315694ac2b",
//        "nextId":"ba072915-d64b-425a-83ce-594c910b4d36",
//        "firstHomeFieldId":"b41ae081-f1d2-45b9-b1dc-e9f7f8d28668",
//        "firstTargetFieldId":"0b35d5cf-e139-4858-9d7b-b5ce07663c63"
//     },
//     {
//        "nodeId":"b41ae081-f1d2-45b9-b1dc-e9f7f8d28668",
//        "previousId":"1372e613-5510-48a0-8b8b-fad3afa92ac1",
//        "nextId":"615fe6f5-2439-403e-8b3d-16f9c683a0f1",
//        "firstHomeFieldId":null,
//        "firstTargetFieldId":null
//     },
//     {
//        "nodeId":"615fe6f5-2439-403e-8b3d-16f9c683a0f1",
//        "previousId":"b41ae081-f1d2-45b9-b1dc-e9f7f8d28668",
//        "nextId":"02039ad0-ce3b-4464-b636-128c42f96bc1",
//        "firstHomeFieldId":null,
//        "firstTargetFieldId":null
//     },
//     {
//        "nodeId":"02039ad0-ce3b-4464-b636-128c42f96bc1",
//        "previousId":"615fe6f5-2439-403e-8b3d-16f9c683a0f1",
//        "nextId":"62542387-e352-478f-b3dc-b3329de6d5b9",
//        "firstHomeFieldId":null,
//        "firstTargetFieldId":null
//     },
//     {
//        "nodeId":"62542387-e352-478f-b3dc-b3329de6d5b9",
//        "previousId":"02039ad0-ce3b-4464-b636-128c42f96bc1",
//        "nextId":null,
//        "firstHomeFieldId":null,
//        "firstTargetFieldId":null
//     },
//     {
//        "nodeId":"0b35d5cf-e139-4858-9d7b-b5ce07663c63",
//        "previousId":"1372e613-5510-48a0-8b8b-fad3afa92ac1",
//        "nextId":"1f00c810-ffc5-434a-8ed2-51561286213c",
//        "firstHomeFieldId":null,
//        "firstTargetFieldId":null
//     },
//     {
//        "nodeId":"1f00c810-ffc5-434a-8ed2-51561286213c",
//        "previousId":"0b35d5cf-e139-4858-9d7b-b5ce07663c63",
//        "nextId":"02404a62-a8e5-427f-b564-79bcb57c8978",
//        "firstHomeFieldId":null,
//        "firstTargetFieldId":null
//     },
//     {
//        "nodeId":"02404a62-a8e5-427f-b564-79bcb57c8978",
//        "previousId":"1f00c810-ffc5-434a-8ed2-51561286213c",
//        "nextId":"50278e84-9f47-4067-b287-b8c23995dc19",
//        "firstHomeFieldId":null,
//        "firstTargetFieldId":null
//     },
//     {
//        "nodeId":"50278e84-9f47-4067-b287-b8c23995dc19",
//        "previousId":"02404a62-a8e5-427f-b564-79bcb57c8978",
//        "nextId":null,
//        "firstHomeFieldId":null,
//        "firstTargetFieldId":null
//     },
//     {
//        "nodeId":"ba072915-d64b-425a-83ce-594c910b4d36",
//        "previousId":"1372e613-5510-48a0-8b8b-fad3afa92ac1",
//        "nextId":"bd9874d4-1113-4440-8584-9b909e08fb9c",
//        "firstHomeFieldId":null,
//        "firstTargetFieldId":null
//     },
//     {
//        "nodeId":"bd9874d4-1113-4440-8584-9b909e08fb9c",
//        "previousId":"ba072915-d64b-425a-83ce-594c910b4d36",
//        "nextId":"39aaf940-d290-4ed8-871a-c6eb7ee1b637",
//        "firstHomeFieldId":null,
//        "firstTargetFieldId":null
//     },
//     {
//        "nodeId":"39aaf940-d290-4ed8-871a-c6eb7ee1b637",
//        "previousId":"bd9874d4-1113-4440-8584-9b909e08fb9c",
//        "nextId":"07c499a7-c6d3-48f5-85ff-8e953b4a2c97",
//        "firstHomeFieldId":null,
//        "firstTargetFieldId":null
//     },
//     {
//        "nodeId":"07c499a7-c6d3-48f5-85ff-8e953b4a2c97",
//        "previousId":"39aaf940-d290-4ed8-871a-c6eb7ee1b637",
//        "nextId":"2ef291cc-5d08-4832-a726-6b5efd7ad587",
//        "firstHomeFieldId":null,
//        "firstTargetFieldId":null
//     },
//     {
//        "nodeId":"2ef291cc-5d08-4832-a726-6b5efd7ad587",
//        "previousId":"07c499a7-c6d3-48f5-85ff-8e953b4a2c97",
//        "nextId":"f41bf9f1-931a-42d4-8e1a-3fbaa0927d41",
//        "firstHomeFieldId":null,
//        "firstTargetFieldId":null
//     },
//     {
//        "nodeId":"f41bf9f1-931a-42d4-8e1a-3fbaa0927d41",
//        "previousId":"2ef291cc-5d08-4832-a726-6b5efd7ad587",
//        "nextId":"d3484d8d-7a17-4162-940e-e6a95ea4549c",
//        "firstHomeFieldId":null,
//        "firstTargetFieldId":null
//     },
//     {
//        "nodeId":"d3484d8d-7a17-4162-940e-e6a95ea4549c",
//        "previousId":"f41bf9f1-931a-42d4-8e1a-3fbaa0927d41",
//        "nextId":"0c701e0f-c4d1-4b14-a684-d071ee45cf62",
//        "firstHomeFieldId":null,
//        "firstTargetFieldId":null
//     },
//     {
//        "nodeId":"0c701e0f-c4d1-4b14-a684-d071ee45cf62",
//        "previousId":"d3484d8d-7a17-4162-940e-e6a95ea4549c",
//        "nextId":"b7c0fb1d-fa1a-43c3-858b-65d7ed90ccc5",
//        "firstHomeFieldId":null,
//        "firstTargetFieldId":null
//     },
//     {
//        "nodeId":"b7c0fb1d-fa1a-43c3-858b-65d7ed90ccc5",
//        "previousId":"0c701e0f-c4d1-4b14-a684-d071ee45cf62",
//        "nextId":"b946f7af-d138-4d2a-b4cd-282810dbd6fb",
//        "firstHomeFieldId":null,
//        "firstTargetFieldId":null
//     },
//     {
//        "nodeId":"b946f7af-d138-4d2a-b4cd-282810dbd6fb",
//        "previousId":"b7c0fb1d-fa1a-43c3-858b-65d7ed90ccc5",
//        "nextId":"1695fbf2-d2e2-4718-a2d0-38c3a26c2c68",
//        "firstHomeFieldId":null,
//        "firstTargetFieldId":null
//     },
//     {
//        "nodeId":"1695fbf2-d2e2-4718-a2d0-38c3a26c2c68",
//        "previousId":"b946f7af-d138-4d2a-b4cd-282810dbd6fb",
//        "nextId":"9a0d19d6-88cd-45f3-b001-63c74fdcf2ef",
//        "firstHomeFieldId":null,
//        "firstTargetFieldId":null
//     },
//     {
//        "nodeId":"9a0d19d6-88cd-45f3-b001-63c74fdcf2ef",
//        "previousId":"1695fbf2-d2e2-4718-a2d0-38c3a26c2c68",
//        "nextId":"383cc77b-5bb3-47aa-b933-961bb67a1446",
//        "firstHomeFieldId":null,
//        "firstTargetFieldId":null
//     },
//     {
//        "nodeId":"383cc77b-5bb3-47aa-b933-961bb67a1446",
//        "previousId":"9a0d19d6-88cd-45f3-b001-63c74fdcf2ef",
//        "nextId":"f63f9c08-1c02-41c8-b686-af96caaf2345",
//        "firstHomeFieldId":null,
//        "firstTargetFieldId":null
//     },
//     {
//        "nodeId":"f63f9c08-1c02-41c8-b686-af96caaf2345",
//        "previousId":"383cc77b-5bb3-47aa-b933-961bb67a1446",
//        "nextId":"6557b4c3-a13c-486a-a6f9-7f315694ac2b",
//        "firstHomeFieldId":null,
//        "firstTargetFieldId":null
//     },
//     {
//        "nodeId":"6557b4c3-a13c-486a-a6f9-7f315694ac2b",
//        "previousId":"f63f9c08-1c02-41c8-b686-af96caaf2345",
//        "nextId":"1372e613-5510-48a0-8b8b-fad3afa92ac1",
//        "firstHomeFieldId":null,
//        "firstTargetFieldId":null
//     }
//  ];