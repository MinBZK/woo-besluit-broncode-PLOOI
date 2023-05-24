import { FileInputAtom } from '../../ui/atoms';
import { CenteredLayout } from '../../ui/layouts';
import { ComponentStory, ComponentMeta } from '@storybook/react';
import { FileReaderUtil } from '../../utils/FileReader';

export default {
    title: 'KOOP-React/Atoms',
    component: FileInputAtom,
    args: {
        children: 'Upload',
        onFileChanged: f => alert(f?.name ?? 'Geen bestand geselecteerd')
    },
} as ComponentMeta<typeof FileInputAtom>

export const InputFile: ComponentStory<typeof FileInputAtom> = (args: any) => <CenteredLayout>
    <FileInputAtom {...args} />
</CenteredLayout>

export const TestUploadFile: ComponentStory<typeof FileInputAtom> = (args: any) => <CenteredLayout>
    <FileInputAtom {...args}
        onFileChanged={async f => {
            if (!f)
                return;

            let fr = new FileReaderUtil(f);
            const test = await fr.ToBinaryString();
        }}
    />
</CenteredLayout>