import {Component, Input, OnInit} from '@angular/core';
import {StateService} from "../../../core/services/state.service";

@Component({
  selector: 'connection-error',
  templateUrl: './connection-error.component.html',
  styleUrls: ['./connection-error.component.css']
})
export class ConnectionErrorComponent implements OnInit {

 @Input() message: string;
  constructor() { }

  ngOnInit(): void {}

}
