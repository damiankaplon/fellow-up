import {APIProvider, Map, MapMouseEvent, Marker} from "@vis.gl/react-google-maps";
import React from "react";
import {Location} from "./MatchmakingWizard.tsx";
import {Button} from "@mui/material";

export interface LocationSelectProps {
  googleMapsApiKey: string;
  onLocationSelect: (location: Location) => void;
}

export function LocationSelect(props: LocationSelectProps) {
  const [location, setLocation] = React.useState<Location>();
  const onMapClick = (ev: MapMouseEvent) =>
    setLocation({lat: ev.detail.latLng!.lat, lng: ev.detail.latLng!.lng});
  return (
    <>
      <div style={{display: "flex", justifyContent: 'center'}}>
        <Button sx={{width: '100%', marginY: '0.5em'}} variant={"outlined"} disabled={location ? undefined : true}
                onClick={() => props.onLocationSelect(location!)}>OK</Button>
      </div>
      {/*<IconButton onClick={() => setMapOpened(!mapOpened)}><MapIcon></MapIcon></IconButton>*/}
      <APIProvider apiKey={props.googleMapsApiKey} onLoad={() => console.log("API LOADED")}
                   onError={() => console.log("API ERROR")}>
        <Map style={{height: '50svh', width: '50svw'}} defaultZoom={18} defaultCenter={{lat: 37.7749, lng: -122.4194}}
             fullscreenControl={true}
             onClick={onMapClick}
        >
          {location && <Marker position={{lat: location.lat, lng: location.lng}}/>}
        </Map>
      </APIProvider>
    </>
  );
}
