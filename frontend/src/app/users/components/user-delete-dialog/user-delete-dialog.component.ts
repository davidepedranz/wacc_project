import { Component, ChangeDetectionStrategy, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';

@Component({
  selector: 'app-user-delete-dialog',
  templateUrl: './user-delete-dialog.component.html',
  styleUrls: ['./user-delete-dialog.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UserDeleteDialogComponent implements OnInit {
  username: string;

  constructor(
    public dialogRef: MatDialogRef<UserDeleteDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    this.username = data.username;
  }

  ngOnInit() {
  }

  onNoClick(): void {
    this.dialogRef.close();
  }
}
