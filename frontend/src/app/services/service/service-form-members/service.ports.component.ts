
import { Component, Input } from '@angular/core';
import { FormGroup } from '@angular/forms';


@Component({
    selector: 'ports-form',
    templateUrl: 'service.ports.component.html'
})
export class portsComponent {
    
    @Input('group')
    public portsForm: FormGroup;
}
