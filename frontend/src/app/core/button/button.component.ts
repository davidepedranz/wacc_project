import { Component } from '@angular/core';

@Component({
    selector: 'Button',
    host: {
        '(onclick)': 'do_action()'
    },
    templateUrl: './button.component.html',
    styleUrls: ['./button.component.css']
})

export class ButtonComponent {
    do_action(number: number) {
        alert('function ' + number + ' processed');
    }
}
