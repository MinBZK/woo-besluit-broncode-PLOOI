import { Component, OnInit } from '@angular/core';
import {LoadingService} from '../../services/loading.service';

@Component({
  selector: 'dcn-loader',
  templateUrl: './loader.component.html',
  styleUrls: ['./loader.component.css']
})
export class LoaderComponent implements OnInit {
  loading$ = this.loader.loading$;
  constructor(public loader: LoadingService) { }

  ngOnInit(): void {
  }

}
