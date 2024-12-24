import {IconButton, TextField} from "@mui/material";
import React, {ChangeEvent} from "react";
import NavigateNextIcon from '@mui/icons-material/NavigateNext';

export interface CategorySelectProps {
    onCategorySelect: (category: string) => void;
    isVisible: boolean;
}

export function CategorySelect(props: CategorySelectProps) {
    const [category, setCategory] = React.useState('');
    const nextButton = (<IconButton>
        <NavigateNextIcon onClick={() => {
            props.onCategorySelect(category)
        }}></NavigateNextIcon>
    </IconButton>);
    return (
        <div style={{visibility: props.isVisible ? 'visible' : 'hidden'}}>
            <TextField id="standard-basic"
                       label="Category"
                       onChange={(event: ChangeEvent<HTMLInputElement>) => setCategory(event.target.value)}
                       variant="standard"
                       sx={{visibility: props.isVisible ? 'visible' : 'hidden'}}
                       slotProps={{input: {endAdornment: nextButton}}}
            />
        </div>
    );

}