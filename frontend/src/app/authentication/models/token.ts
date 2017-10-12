import { User } from '../../users/models/user.model';

export interface Token {
    token: string;
    user: User;
}
