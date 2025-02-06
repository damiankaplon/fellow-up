import {LinearProgress} from "@mui/material";
import {CategorySelect} from "./CategorySelect.tsx";
import React from "react";
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

export interface MatchmakingWizardResult {
  category?: string;
  date?: Date;
  location?: Location;
}

export interface Location {
  lat: number;
  lng: number;
}

export interface MatchmakingWizardProps {
  onComplete: (userInput: MatchmakingWizardResult) => void;
}

export function MatchmakingWizard(props: MatchmakingWizardProps) {
  const [currentContent, setCurrentContent] = React.useState<React.ReactElement>();
  const [wizardState, setWizardState] = React.useState<MatchmakingWizardState>({
    steps: 4,
    currentStep: 1,
    completed: false
  });

  const onCategorySelect = (category: string) => setWizardState(
    (currentState: MatchmakingWizardState) => (
      {...currentState, category: category, currentStep: currentState.currentStep + 1}
    )
  );
  const categorySelect = (<CategorySelect onCategorySelect={onCategorySelect}/>);

  const onDateSelect = (selected: Date) => setWizardState(
    (currentState: MatchmakingWizardState) => (
      {
        ...currentState,
        date: selected,
        currentStep: currentState.currentStep + 1
      }
    )
  );
  const dateSelect = (<DateSelect onDateSelect={onDateSelect}/>);

  const onTimeSelect = (value: Date) => setWizardState(
    (currentState: MatchmakingWizardState) => (
      {
        ...currentState,
        date: value,
        currentStep: currentState.currentStep + 1
      }
    )
  );
  const timeSelect = (<TimeSelect selectedDate={wizardState.date} onTimeSelect={onTimeSelect}/>);

  const onLocationSelect = (location: Location) => setWizardState(
    (currentState: MatchmakingWizardState) => (
      {
        ...currentState,
        location: location,
        completed: true
      }
    )
  );
  const locationSelect = (<LocationSelect googleMapsApiKey={import.meta.env.VITE_GOOGLE_MAPS_API_KEY}
                                          onLocationSelect={onLocationSelect}/>);

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
        category: wizardState.category,
        date: wizardState.date,
        location: wizardState.location
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
