import {LinearProgress} from "@mui/material";
import {CategorySelect} from "./CategorySelect.tsx";
import React, {useReducer} from "react";
import {DateSelect} from "./DateSelect.tsx";
import {TimeSelect} from "./TimeSelect.tsx";
import {LocationSelect} from "./LocationSelect.tsx";

interface MatchmakingWizardState {
  steps: number;
  currentStep: number;
  completed: boolean;
  category?: string;
  date?: Date;
  location?: Location;
}

export interface Location {
  lat: number;
  lng: number;
}

type CategorySelectAction = {
  type: 'CATEGORY_SELECT',
  category: string
}

type DateSelectAction = {
  type: 'DATE_SELECT',
  date: Date
}

type TimeSelectAction = {
  type: 'TIME_SELECT',
  time: Date
}

type LocationSelectAction = {
  type: 'LOCATION_SELECT',
  location: Location
}

type Action = CategorySelectAction | DateSelectAction | TimeSelectAction | LocationSelectAction;

function reducer(state: MatchmakingWizardState, action: Action) {
  if (action.type === 'CATEGORY_SELECT') {
    return {
      ...state,
      category: action.category,
      currentStep: state.currentStep + 1
    }
  }
  if (action.type === 'DATE_SELECT') {
    return {
      ...state,
      date: action.date,
      currentStep: state.currentStep + 1,
    }
  }
  if (action.type === 'TIME_SELECT') {
    return {
      ...state,
      time: action.time,
      currentStep: state.currentStep + 1,
    }
  }
  if (action.type === 'LOCATION_SELECT') {
    return {
      ...state,
      location: action.location,
      currentStep: state.currentStep + 1,
      completed: true
    }
  }
  return state;
}

export interface MatchmakingWizardResult {
  category: string;
  date: Date;
  location: Location;
}

export interface MatchmakingWizardProps {
  onComplete: (userInput: MatchmakingWizardResult) => void;
}

export function MatchmakingWizard(props: MatchmakingWizardProps) {
  const [currentContent, setCurrentContent] = React.useState<React.ReactElement>();
  const [wizardState, dispatch] = useReducer(
    reducer,
    {
      steps: 4,
      currentStep: 1,
      completed: false
    }
  );
  const categorySelect = (
    <CategorySelect
      onCategorySelect={(value: string) => dispatch({type: 'CATEGORY_SELECT', category: value})}/>);

  const dateSelect = (<DateSelect
    onDateSelect={(value: Date) => dispatch({type: 'DATE_SELECT', date: value})}/>);

  const timeSelect = (<TimeSelect selectedDate={wizardState.date}
                                  onTimeSelect={(value: Date) => dispatch({type: 'TIME_SELECT', time: value})}/>);

  const locationSelect = (<LocationSelect googleMapsApiKey={import.meta.env.VITE_GOOGLE_MAPS_API_KEY}
                                          onLocationSelect={(value: Location) => dispatch({
                                            type: 'LOCATION_SELECT',
                                            location: value
                                          })}/>);

  React.useEffect(() => {
    if (wizardState.currentStep == 1) {
      setCurrentContent(categorySelect);
    }
    if (wizardState.currentStep == 2) {
      setCurrentContent(dateSelect);
    }
    if (wizardState.currentStep == 3) {
      setCurrentContent(timeSelect);
    }
    if (wizardState.currentStep == 4) {
      setCurrentContent(locationSelect);
    }
  }, [wizardState.currentStep]);

  React.useEffect(() => {
    if (wizardState.completed == true) {
      props.onComplete({
        category: wizardState.category!,
        date: wizardState.date!,
        location: wizardState.location!
      });
    }
  }, [wizardState.completed]);

  return (
    <>
      <LinearProgress variant="determinate" value={wizardState.currentStep / wizardState.steps * 100}/>
      {currentContent}
    </>
  );
}
