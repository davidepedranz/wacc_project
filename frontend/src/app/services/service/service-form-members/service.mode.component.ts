
import { Component, Input } from '@angular/core';
import { FormGroup } from '@angular/forms';


@Component({
    selector: 'mode-form',
    templateUrl: 'service.mode.component.html'
})
export class modeComponent {
    
    @Input('group')
    public modeForm: FormGroup;
}
