import { FooterMolecule } from '../../ui/molecules';
import { CenteredLayout, DefaultLayout } from '../../ui/layouts';
import { ComponentStory, ComponentMeta } from '@storybook/react';

export default {
    title: 'KOOP-React/Molecules',
    component: FooterMolecule,
    args: {
        menus: [
            [{
                href: "https://google.com",
                text: "Google zoeken",
                lang: 'nl',
                external: false,
                newWindow: true
            }, {
                href: "https://google.com",
                text: "Google zoeken",
                lang: 'nl',
                external: false,
                newWindow: true
            }, {
                href: "https://google.com",
                text: "Google zoeken",
                lang: 'nl',
                external: false,
                newWindow: true
            }, {
                href: "https://google.com",
                text: "Google zoeken",
                lang: 'nl',
                external: false,
                newWindow: true
            }],
            [{
                href: "https://google.com",
                text: "Google zoeken",
                lang: 'nl',
                external: false,
                newWindow: true
            }, {
                href: "https://google.com",
                text: "Google zoeken",
                lang: 'nl',
                external: false,
                newWindow: true
            }, {
                href: "https://google.com",
                text: "Google zoeken",
                lang: 'nl',
                external: false,
                newWindow: true
            }, {
                href: "https://google.com",
                text: "Google zoeken",
                lang: 'nl',
                external: false,
                newWindow: true
            }, {
                href: "https://google.com",
                text: "Google zoeken",
                lang: 'nl',
                external: false,
                newWindow: true
            }]
        ]
    },
} as ComponentMeta<typeof FooterMolecule>

export const Footer: ComponentStory<typeof FooterMolecule> = (args: any) => <DefaultLayout
    header={<></>}
    body={<></>}
    footer={<FooterMolecule {...args} />}
/>