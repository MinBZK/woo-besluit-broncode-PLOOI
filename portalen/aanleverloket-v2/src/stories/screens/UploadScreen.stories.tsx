import { CreateScreen as Screen } from "../../ui/screens";
import { ComponentStory, ComponentMeta } from '@storybook/react';
import { Provider } from "react-redux";
import {setupStore} from "../../store";

export default {
    title: "KOOP-React/Screens",
    component: Screen
} as ComponentMeta<typeof Screen>

export const AanleverFormulier: ComponentStory<typeof Screen> = (args: any) =>
    <Provider store={setupStore()}>
        <Screen></Screen>
    </Provider>;