import {Component, Input, OnInit} from '@angular/core';
import {Page} from "../../../core/models/page";

@Component({
  selector: 'dcn-no-result',
  templateUrl: './no-result.component.html',
  styleUrls: ['./no-result.component.css']
})
export class NoResultComponent implements OnInit {
  @Input() page: Page<any>;
  constructor() { }

  ngOnInit(): void {
  }

}
