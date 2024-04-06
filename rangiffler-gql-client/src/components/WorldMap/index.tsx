import {FC } from 'react';
import SvgWorldMap, {CountryContext} from 'react-svg-worldmap';
import "./styles.css";

type WorldMapData = {
    country: {
        code: string,
    },
    count: number,
}
interface WorldMapInterface {
    data: WorldMapData[],
}
export const WorldMap: FC<WorldMapInterface> = ({data = []}) => {
    const mapData = data.map((v) => ({
        country: v.country.code,
        value: v.count,
    }));

    const getHref = ({ countryCode }: CountryContext) => ({
        id: countryCode,
    });

    return (
            <SvgWorldMap
                color="#174536"
                value-suffix="people"
                size="xl"
                data={mapData}
                richInteraction={true}
                hrefFunction={getHref}
            />
    );
}
