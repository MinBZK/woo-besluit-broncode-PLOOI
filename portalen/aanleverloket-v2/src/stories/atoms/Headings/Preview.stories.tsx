import { HeadingPreviewAtom } from '../../../ui/atoms';
import { CenteredLayout } from '../../../ui/layouts';
import { ComponentStory, ComponentMeta } from '@storybook/react';

export default {
    title: 'KOOP-React/Atoms/Headings',
    component: HeadingPreviewAtom,
    args: { 
        children: 'Heading Preview'
    },
} as ComponentMeta<typeof HeadingPreviewAtom>

export const Preview: ComponentStory<typeof HeadingPreviewAtom> = (args: any) => <CenteredLayout>
    <HeadingPreviewAtom {...args} />
</CenteredLayout>