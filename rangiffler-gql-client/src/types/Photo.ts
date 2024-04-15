import { Country } from "./Country";
import { Likes } from "./Likes";

export type Photo = {
    canEdit: boolean;
    id: string;
    src: string;
    country: Country;
    description: string;
    likes: Likes;

}