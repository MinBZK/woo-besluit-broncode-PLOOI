import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, NavigationEnd, Router} from '@angular/router';
import {filter} from 'rxjs/operators';
import {MenuItem} from './MenuItem';

@Component({
  selector: 'dcn-breadcrumb',
  templateUrl: './breadcrumb.component.html',
  styleUrls: ['./breadcrumb.component.css']
})
export class BreadcrumbComponent implements OnInit {
    static readonly ROUTE_DATA_BREADCRUMB = 'breadcrumb';
    menuItems: MenuItem[];

    constructor(private router: Router, private activatedRoute: ActivatedRoute) {}

    ngOnInit(): void {
        this.router.events
            .pipe(filter(event => event instanceof NavigationEnd))
            .subscribe(() => this.menuItems = this.createBreadcrumbs(this.activatedRoute.root));
        if(this.menuItems == undefined ) {
            this.menuItems = this.createBreadcrumbs(this.activatedRoute);
        }
    }

    private createBreadcrumbs(route: ActivatedRoute, url: string = '', breadcrumbs: MenuItem[] = []): MenuItem[] {
        const children: ActivatedRoute[] = route.children;

        if (children.length === 0) {
            return breadcrumbs;
        }

        for (const child of children) {
            const routeURL: string = child.snapshot.url.map(segment => segment.path).join('/');
            if (routeURL !== '') {
                url += `/${routeURL}`;
            }
            let label = child.snapshot.data[BreadcrumbComponent.ROUTE_DATA_BREADCRUMB];
            if (label == 'Mappingfouten' && child.snapshot.params['type']=== 'WARNING') {
                label = 'Mappingwaarschuwingen';
            }
            if (label == 'Veldfouten' && child.snapshot.parent.params['type']=== 'WARNING') {
                label = 'Veldwaarschuwingen';
            }
            breadcrumbs.push({label, url });

            return this.createBreadcrumbs(child, url, breadcrumbs);
        }
    }
}