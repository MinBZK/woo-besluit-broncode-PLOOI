import {Injectable} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ModalService {
    constructor() {}

     updateSelectedModel(idAttr) {
        if(idAttr) {
            Array.from(document.getElementsByClassName("selected-modal")).forEach((el: Element) => {
                el.classList.remove("selected-modal")
            })
            document.getElementById(idAttr).className += " selected-modal";
        }
    }

    scrollToSelectedModal(idAttr) {
        if(idAttr) {
            const yOffset = -100;
            const element = document.getElementById(idAttr);
            const y = element.getBoundingClientRect().top + window.pageYOffset + yOffset;
            window.scrollTo({top: y});
        }
    }
}
