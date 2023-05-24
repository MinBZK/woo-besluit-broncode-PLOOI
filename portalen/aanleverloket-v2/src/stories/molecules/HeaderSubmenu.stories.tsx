import { HeaderSubmenuMolecule } from '../../ui/molecules';
import { DefaultLayout } from '../../ui/layouts';
import { ComponentStory, ComponentMeta } from '@storybook/react';

export default {
    title: 'KOOP-React/Molecules',
    component: HeaderSubmenuMolecule,
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
} as ComponentMeta<typeof HeaderSubmenuMolecule>

export const HeaderSubmenu: ComponentStory<typeof HeaderSubmenuMolecule> = (args: any) => <DefaultLayout
    header={<HeaderSubmenuMolecule {...args} />}
    body={<></>}
    footer={<></>}
/>