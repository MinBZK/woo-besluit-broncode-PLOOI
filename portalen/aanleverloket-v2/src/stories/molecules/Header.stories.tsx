import { HeaderMolecule } from '../../ui/molecules';
import { DefaultLayout } from '../../ui/layouts';
import { ComponentStory, ComponentMeta } from '@storybook/react';
import { Provider } from 'react-redux';
import { setupStore } from '../../store';
import { BrowserRouter as Router } from "react-router-dom";

export default {
    title: 'KOOP-React/Molecules',
    component: HeaderMolecule,
    args: {
        logo: "https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png",
        subtitle: "Ondertitel",
        links: [
            {
                href: "https://google.com",
                text: "Google zoeken",
                lang: 'nl',
                external: false,
                newWindow: true
            }
        ]
    },
} as ComponentMeta<typeof HeaderMolecule>

export const Header: ComponentStory<typeof HeaderMolecule> = (args: any) => <DefaultLayout  
    header={<Router><Provider store={setupStore()}><HeaderMolecule {...args} /></Provider></Router>}
    body={<></>}
    footer={<></>}
/>