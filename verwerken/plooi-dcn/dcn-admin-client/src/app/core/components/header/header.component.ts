import {Component, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {TokenStorageService} from "../../services/token-storage.service";

@Component({
  selector: 'admin-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {
  username: string;
  constructor(private router: Router, private tokenStorage: TokenStorageService) { }

  ngOnInit(): void {
    this.username = this.tokenStorage.getUser();
  }

  logout() {
    this.tokenStorage.logout();
  }
}
