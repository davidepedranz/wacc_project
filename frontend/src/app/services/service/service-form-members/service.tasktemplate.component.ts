
import { Component, Input } from '@angular/core';
import { FormGroup } from '@angular/forms';


@Component({
    selector: 'task-template-form',
    templateUrl: 'service.tasktemplate.component.html'
})
export class taskTemplateComponent {
    
    @Input('group')
    public taskTemplateForm: FormGroup;
}
