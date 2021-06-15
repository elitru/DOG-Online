import { Component, OnInit } from '@angular/core';
import { DialogService } from 'src/app/provider/dialog.service';

@Component({
  selector: 'app-dialog',
  templateUrl: './dialog.component.html',
  styleUrls: ['./dialog.component.styl']
})
export class DialogComponent implements OnInit {

  constructor(public dialogService: DialogService) { }

  public ngOnInit(): void {
  
  }
}
